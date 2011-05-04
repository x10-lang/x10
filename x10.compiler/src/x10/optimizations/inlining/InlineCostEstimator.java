/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.optimizations.inlining;

import static x10cpp.visit.ASTQuery.assertNumberOfInitializers;
import static x10cpp.visit.ASTQuery.getStringPropertyInit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Special;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.visit.ExpressionFlattener;
import x10.visit.X10DelegatingVisitor;

/**
 * @author Bowen Alpern
 *
 */
class InlineCostEstimator extends X10DelegatingVisitor {

    private static final int          NATIVE_CODE_COST  = 989898;
    private static final List<String> javaNativeStrings = Arrays.asList("java");
    private static final List<String> cppNativeStrings  = Arrays.asList("c++", "cuda");

    private final InlineCostVisitor visitor;
    private final Job job;

    private int cost;
    
    InlineCostEstimator (Job job, TypeSystem ts, NodeFactory nf){
        visitor  = new InlineCostVisitor(job, ts, nf, this);
        this.job = job;
    }
    
    synchronized int getCost(Node n, Job job) {
        if (null == n) return 0;
        cost = 0;
        n.visit(visitor);
        return cost;
    }

    public final void visit(ProcedureCall c) {
        cost++;
        visit((Node) c);
    }

    public final void visit(ClassMember c) { // never inline these ??
        cost += 100;
        visit((Node) c);
    }

    public final void visit(Special c) {
        cost++;
        visit((Node) c);
    }

    public final void visit(Node n) {
        if (isNativeCode(n))
            cost = NATIVE_CODE_COST;
    }

    private boolean isNativeCode(Node n) {
        return !getApplicableNativeAnnotations(n, job).isEmpty();
    }

    public static List<X10ClassType> getApplicableNativeAnnotations (Node node, Job job) {
        List<X10ClassType> result = new ArrayList<X10ClassType>();
        assert node.ext() instanceof X10Ext;
        List<X10ClassType> annotations = ((X10Ext) node.ext()).annotationMatching(job.extensionInfo().typeSystem().NativeType());
        for (X10ClassType annotation : annotations) {
            assertNumberOfInitializers(annotation, 2);
            String platform = getStringPropertyInit(annotation, 0);
            List<String> nativeStrings = (ExpressionFlattener.javaBackend(job) ? javaNativeStrings : cppNativeStrings);
            for (String ns : nativeStrings) {
                if (platform != null && platform.equals(ns)) {
                    result.add(annotation);
                }
            }
        }
        return result;
    }

}

class InlineCostVisitor extends NodeVisitor {
    InlineCostEstimator ice;

    /**
     * @param job
     * @param ts
     * @param nf
     */
    InlineCostVisitor(Job job, TypeSystem ts, NodeFactory nf, InlineCostEstimator ce) {
        ice = ce;
    }

    /*
     * (non-Javadoc)
     * 
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node,
     * polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        ice.visitAppropriate(n);
        return n;
    }
}
