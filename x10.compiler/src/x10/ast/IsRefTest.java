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
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.constants.ConstantValue;

/**
 * Represents the isref type constraint.  Based on HasZeroTest
 * 
 * @author Dave Cunningham July 20, 2012
 * 
 */
public class IsRefTest extends Expr_c implements Expr {

	TypeNode param;

	public IsRefTest(Position pos, TypeNode param) {
		super(pos);
		this.param = param;
	}

	public TypeNode parameter() {
		return param;
	}
	public IsRefTest parameter(TypeNode param) {
		IsRefTest n = (IsRefTest) copy();
		n.param = param;
		return n;
	}

	/** Reconstruct the statement. */
	protected IsRefTest reconstruct(TypeNode param) {
		if (param != this.param) {
			IsRefTest n = (IsRefTest) copy();
			n.param = param;
			return n;
		}

		return this;
	}

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		TypeNode sub = (TypeNode) visitChild(this.param, v);
		return reconstruct(sub);
	}

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) {
		TypeSystem ts = tc.typeSystem();
		return type(ts.Boolean());
	}

	public boolean isConstant() {
		return false;
	}

	public ConstantValue constantValue() {
		return null;
	}

	public Term firstChild() {
		return param;
	}


	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(param, this, EXIT);
		return succs;
	}

	public String toString() {
		return param + " isRef";
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		printBlock(param, w, tr);
		w.write(" isRef");
	}

}
