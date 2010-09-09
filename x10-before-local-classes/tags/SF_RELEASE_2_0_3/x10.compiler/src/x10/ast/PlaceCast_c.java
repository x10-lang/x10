/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.visit.ExprFlattener;

/** An immutable representation of the X10 construct (@place) expr. 
 * @author vj May 18, 2005
 * 
 */
public class PlaceCast_c extends Expr_c implements PlaceCast {

	Expr place;
	Expr expr;
	/**
	 * @param pos
	 */
	public PlaceCast_c(Position pos, Expr place, Expr expr) {
		super(pos);
		this.expr = expr;
		this.place= place;
	}

	/** Reconstruct the expression. */
	protected PlaceCast_c reconstruct(Expr place, Expr expr) {
		if (place != this.place || expr != this.expr) {
			PlaceCast_c n = (PlaceCast_c) copy();
			n.place = place;
			n.expr = expr;
			return n;
		}
		
		return this;
	}
	
	/** Visit the children of the expression. */
	public Node visitChildren(NodeVisitor v) {
		Expr place = (Expr) visitChild(this.place, v);
		Expr expr = (Expr) visitChild(this.expr, v);
		Node result = reconstruct(place, expr);
		if (v instanceof ExprFlattener.Flattener ) {
			//Report.report(1, "PlaceCast_c flattened to |" + result + "|");
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	
	 public Term firstChild() {
        return expr;
    }
	

	 /** Get the precedence of the expression. */
	 public Precedence precedence() {
	 	return Precedence.CAST;
	 }
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	 public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(place, expr, ENTRY);
        v.visitCFG(expr, this, EXIT);
        return succs;
    }


	/* (non-Javadoc)
	 * @see x10.ast.PlaceCast#placeCastType()
	 */
	public Expr placeCastType() {
		return place;
	}

	/* (non-Javadoc)
	 * @see x10.ast.PlaceCast#castType(polyglot.ast.Expr)
	 */
	public PlaceCast placeCastType(Expr place) {
		PlaceCast_c n = (PlaceCast_c) copy();
		n.place = place;
		return n;
	}

	/* (non-Javadoc)
	 * @see x10.ast.PlaceCast#expr()
	 */
	public Expr expr() {
		return expr;
	}

	/* (non-Javadoc)
	 * @see x10.ast.PlaceCast#expr(polyglot.ast.Expr)
	 */
	public PlaceCast expr(Expr expr) {
		PlaceCast_c n = (PlaceCast_c) copy();
		n.expr = expr;
		return n;
	}
	public Node typeCheck(ContextVisitor tc) throws SemanticException {

	    return type(expr.type());
	}
	public String toString() {
	    return "(@" + place + ") " + expr;
	}
}
