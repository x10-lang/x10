/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.types.constants.BooleanValue;
import x10.types.constants.ConstantValue;

/**
 * A <code>Conditional</code> is a representation of a Java ternary
 * expression.  That is, <code>(cond ? consequent : alternative)</code>.
 */
public abstract class Conditional_c extends Expr_c implements Conditional
{
    protected Expr cond;
    protected Expr consequent;
    protected Expr alternative;

    public Conditional_c(Position pos, Expr cond, Expr consequent, Expr alternative) {
	super(pos);
	assert(cond != null && consequent != null && alternative != null);
	this.cond = cond;
	this.consequent = consequent;
	this.alternative = alternative;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() { 
	return Precedence.CONDITIONAL;
    }

    /** Get the conditional of the expression. */
    public Expr cond() {
	return this.cond;
    }

    /** Set the conditional of the expression. */
    public Conditional cond(Expr cond) {
	Conditional_c n = (Conditional_c) copy();
	n.cond = cond;
	return n;
    }

    /** Get the consequent of the expression. */
    public Expr consequent() {
	return this.consequent;
    }

    /** Set the consequent of the expression. */
    public Conditional consequent(Expr consequent) {
	Conditional_c n = (Conditional_c) copy();
	n.consequent = consequent;
	return n;
    }

    /** Get the alternative of the expression. */
    public Expr alternative() {
	return this.alternative;
    }

    /** Set the alternative of the expression. */
    public Conditional alternative(Expr alternative) {
	Conditional_c n = (Conditional_c) copy();
	n.alternative = alternative;
	return n;
    }

    /** Reconstruct the expression. */
    protected Conditional_c reconstruct(Expr cond, Expr consequent, Expr alternative) {
	if (cond != this.cond || consequent != this.consequent || alternative != this.alternative) {
	    Conditional_c n = (Conditional_c) copy();
	    n.cond = cond;
	    n.consequent = consequent;
	    n.alternative = alternative;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	Expr cond = (Expr) visitChild(this.cond, v);
	Expr consequent = (Expr) visitChild(this.consequent, v);
	Expr alternative = (Expr) visitChild(this.alternative, v);
	return reconstruct(cond, consequent, alternative);
    }

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
	return cond + " ? " + consequent + " : " + alternative;
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr)
    {
	printSubExpr(cond, false, w, tr);
        w.unifiedBreak(2);
	w.write("? ");
	printSubExpr(consequent, false, w, tr);
        w.unifiedBreak(2);
	w.write(": ");
	printSubExpr(alternative, false, w, tr);
    }

    public Term firstChild() {
        return cond;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent,
                         ENTRY, FlowGraph.EDGE_KEY_FALSE, alternative, ENTRY);
        v.visitCFG(consequent, this, EXIT);
        v.visitCFG(alternative, this, EXIT);

        return succs;
    }
    
    public boolean isConstant() {
	return cond.isConstant() && consequent.isConstant() && alternative.isConstant();
    }

    public ConstantValue constantValue() {
        ConstantValue cond_ = cond.constantValue();
        ConstantValue then_ = consequent.constantValue();
        ConstantValue else_ = alternative.constantValue();

        if (cond_ instanceof BooleanValue && then_ != null && else_ != null) {
            boolean c = ((BooleanValue)cond_).value();
            if (c) {
                return then_;
            }
            else {
                return else_;
            }
        }

        return null;
    }

}
