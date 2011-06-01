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

import polyglot.ast.Call_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.visit.ExpressionFlattener;
import x10.visit.X10DelegatingVisitor;

/**
 * @author Bowen Alpern
 *
 */
class InlineCostEstimator extends NodeVisitor {

    private static final int          NATIVE_CODE_COST  = 0x1000;
    private static final List<String> javaNativeStrings = Arrays.asList("java");
    private static final List<String> cppNativeStrings  = Arrays.asList("c++", "cuda");

    private final InlineCostDelegate delegate;

    int cost[] = new int[1];;
    
    InlineCostEstimator (){
        delegate = new InlineCostDelegate(this);
    }
    
    synchronized int getCost(Node n, Job job) {
        if (null == n) return 0;
        if (isNativeCode(n, job)) return NATIVE_CODE_COST;
        cost[0] = 0;
        n.visit(this);
        return cost[0];
    }

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#leave(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        delegate.visitAppropriate(n);
        return n;
    }

    private boolean isNativeCode(Node n, Job job) {
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

final class InlineCostDelegate extends X10DelegatingVisitor{

    final InlineCostEstimator ice;

    /**
     * @param ce
     */
    InlineCostDelegate(InlineCostEstimator ce) {
        ice = ce;
    }

    public final void visit(AssignPropertyCall_c c) {
    }

    public final void visit(Call_c c) {
        ice.cost[0]++;
    }

    public final void visit(ConstructorCall_c c) {
        ice.cost[0]++;
    }

}
