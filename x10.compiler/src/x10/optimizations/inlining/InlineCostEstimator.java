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
import polyglot.ast.Special;
import polyglot.frontend.Job;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.visit.ExpressionFlattener;
import x10.visit.X10DelegatingVisitor;

/**
 * @author Bowen Alpern
 *
 */
class InlineCostEstimator extends NodeVisitor {

    static final int MAX_ACTUAL_COST   = 0x0FFFF;
    static final int NATIVE_CODE_COST  = 0x10000;
    static final int JAVA_SPECIAL_COST = 0x20000;

    InlineCostDelegate delegate;
    int cost[] = new int[1];
    Job job;
    boolean hasSuper = false;
    InlineAnnotationUtils annotations;
    
    InlineCostEstimator(Job j) {
        job = j;
        annotations = new InlineAnnotationUtils(job);
        delegate = new InlineCostDelegate(this);
    }

    int getCost() {
        return cost[0];
    }

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#leave(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof Special && ((Special) n).kind() == Special.SUPER && ExpressionFlattener.javaBackend(job)) {
            cost[0] |= JAVA_SPECIAL_COST; // Java back-end cannot handle inlined super targets
        }
        if (annotations.isNativeCode(n)) {
            cost[0] |= NATIVE_CODE_COST;
        } else {
            delegate.visitAppropriate(n);
        }
        return n;
    }

}

final class InlineCostDelegate extends X10DelegatingVisitor{

    final InlineCostEstimator ice;

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
