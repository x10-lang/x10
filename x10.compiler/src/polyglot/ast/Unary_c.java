/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.types.constants.BooleanValue;
import x10.types.constants.CharValue;
import x10.types.constants.ConstantValue;
import x10.types.constants.DoubleValue;
import x10.types.constants.FloatValue;
import x10.types.constants.IntegralValue;

/**
 * A <code>Unary</code> represents a Java unary expression, an
 * immutable pair of an expression and an operator.
 */
public abstract class Unary_c extends Expr_c implements Unary
{
    protected Unary.Operator op;
    protected Expr expr;

    public Unary_c(Position pos, Unary.Operator op, Expr expr) {
	super(pos);
	assert(op != null && expr != null);
	this.op = op;
	this.expr = expr;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
	return Precedence.UNARY;
    }

    /** Get the sub-expression of the expression. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the sub-expression of the expression. */
    public Unary expr(Expr expr) { Unary_c n = (Unary_c) copy(); n.expr = expr;
      return n; }

    /** Get the operator. */
    public Unary.Operator operator() {
	return this.op;
    }

    /** Set the operator. */
    public Unary operator(Unary.Operator op) {
	Unary_c n = (Unary_c) copy();
	n.op = op;
	return n;
    }

    /** Reconstruct the expression. */
    protected Unary_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Unary_c n = (Unary_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) visitChild(this.expr, v);
	return reconstruct(expr);
    }

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);

    /** Check exceptions thrown by the statement. */
    public String toString() {
        if (op == NEG && expr instanceof IntLit && ((IntLit) expr).boundary()) {
            return op.toString() + ((IntLit) expr).positiveToString();
        }
        else if (op.isPrefix()) {
        	boolean needParen = op != PRE_INC && op != PRE_DEC;
        	return op.toString() + (needParen ? "(" : "") + expr.toString() + (needParen ? ")" : "");
	}
	else {
    	boolean needParen = op != POST_INC && op != POST_DEC;
	    return (needParen ? "(" : "") + expr.toString() + (needParen ? ")" : "") + op.toString();
	}
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (op == NEG && expr instanceof IntLit && ((IntLit) expr).boundary()) {
	    w.write(op.toString());
            w.write(((IntLit) expr).positiveToString());
        }
        else if (op.isPrefix()) {
	    w.write(op.toString());
	    printSubExpr(expr, false, w, tr);
	}
	else {
	    printSubExpr(expr, false, w, tr);
	    w.write(op.toString());
	}
    }

    public Term firstChild() {
        return expr;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (expr.type().isBoolean()) {
            v.visitCFG(expr, FlowGraph.EDGE_KEY_TRUE, this,
                             EXIT, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
        } else {
            v.visitCFG(expr, this, EXIT);
        }
        
        return succs;
    }
    
    public boolean isConstant() {
	if (op == POST_INC || op == POST_DEC ||
	    op == PRE_INC || op == PRE_DEC) {
            return false;
        }
	return expr.isConstant();
    }

    public ConstantValue constantValue() {
        if (! isConstant()) {
            return null;
        }

        ConstantValue v = expr.constantValue();

        if (v instanceof BooleanValue) {
            boolean vv = ((BooleanValue) v).value();
            if (op == NOT) return ConstantValue.makeBoolean(!vv);
        }
        
        if (v instanceof DoubleValue) {
            double vv = ((DoubleValue) v).value();
            if (op == POS) return v;
            if (op == NEG) return ConstantValue.makeDouble(-vv);
        }
        
        if (v instanceof FloatValue) {
            float vv = ((FloatValue) v).value();
            if (op == POS) return v;
            if (op == NEG) return ConstantValue.makeFloat(-vv);
        }
        
        if (v instanceof IntegralValue) {
            IntegralValue iv = ((IntegralValue)v);
            if (iv.isULong() || iv.isLong()) {
                if (op == BIT_NOT) return ConstantValue.makeIntegral(~iv.longValue(), iv.kind());
                if (op == POS) return v;
                if (op == NEG) return ConstantValue.makeIntegral(-iv.longValue(), iv.kind());
            } else if (iv.isInt() || iv.isUInt()) {
                if (op == BIT_NOT) return ConstantValue.makeIntegral(~iv.intValue(), iv.kind());
                if (op == POS) return v;
                if (op == NEG) return ConstantValue.makeIntegral(-iv.intValue(), iv.kind());
            } else if (iv.isShort() || iv.isUShort()) {
                if (op == BIT_NOT) return ConstantValue.makeIntegral(~iv.shortValue(), iv.kind());
                if (op == POS) return v;
                if (op == NEG) return ConstantValue.makeIntegral(-iv.shortValue(), iv.kind());
            } else {
                if (op == BIT_NOT) return ConstantValue.makeIntegral(~iv.byteValue(), iv.kind());
                if (op == POS) return v;
                if (op == NEG) return ConstantValue.makeIntegral(-iv.byteValue(), iv.kind());
            }
        }
        
        if (v instanceof CharValue) {
            CharValue cv = (CharValue)v;
            if (op == BIT_NOT) return ConstantValue.makeChar((char)(~cv.value()));
            if (op == POS) return v;
            if (op == NEG) return ConstantValue.makeChar((char)(-cv.value()));
            
        }

        // not a constant
        return null;
    }
}
