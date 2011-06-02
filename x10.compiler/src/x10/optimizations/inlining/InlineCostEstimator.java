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


import java.util.Arrays;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.visit.X10DelegatingVisitor;

/**
 * @author Bowen Alpern
 *
 */
class InlineCostEstimator extends NodeVisitor {

    static final int NATIVE_CODE_COST = 0x1000;

    InlineCostDelegate delegate;
    int cost[] = new int[1];
    Job job;
    
    InlineCostEstimator(Job j) {
        job = j;
        if (null == delegate) {
            delegate = new InlineCostDelegate(this);
        }
    }

    int getCost() {
        return cost[0];
    }

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#leave(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (isNativeCode(n)) {
            cost[0] |= NATIVE_CODE_COST;
        } else {
            delegate.visitAppropriate(n);
        }
        return n;
    }

    boolean isNativeCode(Node n) {
        return !AnnotationUtils.getNativeAnnotations(n, job).isEmpty();
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

    /**
     * Property calls are not charged
     */
    public final void visit(AssignPropertyCall_c c) {
    }

    /**
     * Method calls are charged 1
     */
    public final void visit(Call_c c) {
        ice.cost[0]++;
    }

    /**
     * Constructor calls are charged 1
     */
    public final void visit(ConstructorCall_c c) {
        ice.cost[0]++;
    }

}
