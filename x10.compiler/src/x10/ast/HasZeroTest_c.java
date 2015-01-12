/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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
 * @author vj Feb 4, 2005
 * 
 */
public class HasZeroTest_c extends Expr_c implements HasZeroTest {

	TypeNode param;

	public HasZeroTest_c(Position pos, TypeNode param) {
		super(pos);
		this.param = param;
	}

	public TypeNode parameter() {
		return param;
	}
	public HasZeroTest_c parameter(TypeNode param) {
		HasZeroTest_c n = (HasZeroTest_c) copy();
		n.param = param;
		return n;
	}

	/** Reconstruct the statement. */
	protected HasZeroTest_c reconstruct(TypeNode param) {
		if (param != this.param) {
			HasZeroTest_c n = (HasZeroTest_c) copy();
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

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return param;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(param, this, EXIT);
		return succs;
	}

	public String toString() {
		return param + " hasZero";
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		printBlock(param, w, tr);
		w.write(" hasZero");
	}

}
