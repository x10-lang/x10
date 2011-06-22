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


import polyglot.ast.Assign_c;
import polyglot.ast.Call_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldDecl;
import polyglot.ast.Lit_c;
import polyglot.ast.Local_c;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Special;
import polyglot.frontend.Job;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.ast.ClosureCall_c;
import x10.ast.StmtExpr_c;
import x10.visit.ExpressionFlattener;
import x10.visit.X10DelegatingVisitor;

/**
 * @author Bowen Alpern
 *
 */
class InlineCostEstimator extends NodeVisitor {
    
    static final boolean XTENLANG_2818 = true; // FIXME: Java back-end does not support non-virtual instance calls
    static final boolean XTENLANG_2819 = true; // FIXME: C++  back-end generates incorrect code for embedded fields

    static final int MAX_ACTUAL_COST  = 0x0FFFF;
    static final int NATIVE_CODE_COST = 0x10000;
    static final int JAVA_SUPER_COST  = 0x20000; // see XTENLANG_2818
    static final int CPP_EMBED_COST   = 0x40000; // see XTENLANG_2819

    InlineCostDelegate delegate;
    int cost[] = new int[1];
    ProcedureDecl decl;
    Job job;
    InlineAnnotationUtils annotations;
    
    InlineCostEstimator(Job j, ProcedureDecl pd) {
        job         = j;
        decl        = pd;
        annotations = new InlineAnnotationUtils(job);
        delegate    = new InlineCostDelegate(this);
    }

    int getCost() {
        return cost[0];
    }

    ProcedureDecl getDecl() {
        return decl;
    }
    
    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#leave(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (XTENLANG_2818 && n instanceof Special && ((Special) n).kind() == Special.SUPER && ExpressionFlattener.javaBackend(job)) {
            cost[0] |= JAVA_SUPER_COST; // Java back-end cannot handle inlined super targets
        } else if (XTENLANG_2818 && n instanceof ConstructorCall && ((ConstructorCall) n).kind() == ConstructorCall.SUPER && ExpressionFlattener.javaBackend(job)) {
            cost[0] |= JAVA_SUPER_COST; // Java back-end cannot handle inlined super calls either
        } else if (XTENLANG_2819 && annotations.hasEmbedAnnotation(n) && !ExpressionFlattener.javaBackend(job)) {
            cost[0] |= CPP_EMBED_COST;      // C++  back-end cannot handle embedded fields
        } else if (annotations.isNativeCode(n)) {
            cost[0] |= NATIVE_CODE_COST;
        } else {
            delegate.visitAppropriate(n);
        }
        return n;
    }

}

final class InlineCostDelegate extends X10DelegatingVisitor{
    static final int CALL_COST        = 0x10;
    static final int OPERATION_COST   = 2;
    static final int SMALL_COST       = 1;
    static final int NO_COST          = 0;

    final InlineCostEstimator ice;

    InlineCostDelegate(InlineCostEstimator ce) {
        ice = ce;
    }

    /**
     * Property calls are not charged.
     *
     * @see x10.visit.X10DelegatingVisitor#visit(x10.ast.AssignPropertyCall_c)
     */
    public final void visit(AssignPropertyCall_c c) {
        ice.cost[0] += NO_COST;
    }

    /**
     * Closure calls are charged less than other calls.
     * 
     * @see x10.visit.X10DelegatingVisitor#visit(x10.ast.ClosureCall_c)
     */
    @Override
    public void visit(ClosureCall_c n) { // todo handle cases separately
        ice.cost[0] += OPERATION_COST;
    }

    /**
     * Constructor calls are charged 16.
     * 
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.ConstructorCall_c)
     */
    public final void visit(ConstructorCall_c c) { // todo cc are cheaper
        ice.cost[0] += CALL_COST;
    }

    /**
     * Method calls are charged 16.
     * 
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.Call_c)
     */
    public final void visit(Call_c c) { // TODO handle target Arithmetic, boolean, and Char or @intrinsic
        ice.cost[0] += CALL_COST;
    }

    /**
     * Literals are not charged.
     * 
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.Lit_c)
     */
    public final void visit(Lit_c l) {
        ice.cost[0] += NO_COST;
    }

    /**
     * Locals are not charged.
     */
    public final void visit(Local_c l) {
        ice.cost[0] += NO_COST;
    }

    /**
     * Assignments are not charged.
     * 
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.Assign_c)
     */
    @Override
    public void visit(Assign_c n) {
        ice.cost[0] += NO_COST;
    }

    /**
     * Statement Expressions are not charged.
     *
     * @see x10.visit.X10DelegatingVisitor#visit(x10.ast.StmtExpr_c)
     */
    @Override
    public void visit(StmtExpr_c n) {
        ice.cost[0] += NO_COST;
    }

    /**
     * Other expressions are charged 2.
     *
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.Expr_c)
     */
    public final void visit(Expr_c e) {
        ice.cost[0] += OPERATION_COST;
    }

    /**
     * Other nodes are not charged.
     *
     * @see x10.visit.X10DelegatingVisitor#visit(polyglot.ast.Node_c)
     */
    public final void visit(Node_c n) {
        ice.cost[0] += NO_COST;
    }
}
