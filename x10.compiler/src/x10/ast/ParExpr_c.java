/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.ast.Unary;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.FlowGraph;
import x10.types.constants.ConstantValue;

/**
 * @author vj Feb 4, 2005
 * 
 */
public class ParExpr_c extends Expr_c implements ParExpr {

	Expr expr;
	/**
	 * @param pos
	 */
	public ParExpr_c(Position pos, Expr expr) {
		super(pos);
		this.expr = expr;
		
	}
	public Expr expr() {
		return expr;
	}
	public ParExpr expr( Expr expr) {
		ParExpr_c n = (ParExpr_c) copy();
		n.expr = expr;
		return n;
	}

	 /** Reconstruct the statement. */
    protected ParExpr_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    ParExpr_c n = (ParExpr_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the parenthesized expression. */
    public Node visitChildren(NodeVisitor v) {
    	Expr expr = (Expr) visitChild(this.expr, v);
    	return reconstruct(expr);
    }
    
    /** Type check the parenthesized expression. */
    public Node typeCheck(ContextVisitor tc) {  	
    	return type(expr.type());
    }
    
    public boolean isConstant() {
    	return expr.isConstant();
    }

    public ConstantValue constantValue() {
        if (! isConstant()) {
        	return null;
        }
        
    	return expr.constantValue();
    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return expr;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 * todo Yoav: I just copied it from Unary_c. I think that ParExpr_c should inherit from Unary_c, and we should define Unary.Operator.IDEMPOTENT.
	 */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (expr.type().isBoolean()) {
            v.visitCFG(expr, FlowGraph.EDGE_KEY_TRUE, this,
                             EXIT, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
        } else {
            v.visitCFG(expr, this, EXIT);
        }

        return succs;
    }
	public String toString() {
		return "(" + expr.toString() + ")";
	}
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("(");
		printBlock(expr, w, tr);
		w.write(")");
	}

}
