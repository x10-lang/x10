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
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.errors.Errors;
import x10.types.constants.ConstantValue;

/**
 * @author vj Feb 4, 2005
 * 
 */
public class SubtypeTest_c extends Expr_c implements SubtypeTest {

	TypeNode sub;
	TypeNode sup;
	boolean equals;

	/**
	 * @param pos
	 * @param equals TODO
	 */
	public SubtypeTest_c(Position pos, TypeNode sub, TypeNode sup, boolean equals) {
		super(pos);
		this.sub = sub;
		this.sup = sup;
		this.equals = equals;
	}
	
	public boolean equals() {
	    return equals;
	}

	public TypeNode supertype() {
		return sup;
	}

	public TypeNode subtype() {
		return sub;
	}

	public SubtypeTest equals(boolean equals) {
	    SubtypeTest_c n = (SubtypeTest_c) copy();
	    n.equals = equals;
	    return n;
	}
	
	public SubtypeTest supertype(TypeNode sup) {
		SubtypeTest_c n = (SubtypeTest_c) copy();
		n.sup = sup;
		return n;
	}

	public SubtypeTest subtype(TypeNode sub) {
		SubtypeTest_c n = (SubtypeTest_c) copy();
		n.sub = sub;
		return n;
	}

	/** Reconstruct the statement. */
	protected SubtypeTest_c reconstruct(TypeNode sub, TypeNode sup) {
		if (sub != this.sub || sup != this.sup) {
			SubtypeTest_c n = (SubtypeTest_c) copy();
			n.sub = sub;
			n.sup = sup;
			return n;
		}

		return this;
	}

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		TypeNode sub = (TypeNode) visitChild(this.sub, v);
		TypeNode sup = (TypeNode) visitChild(this.sup, v);
		return reconstruct(sub, sup);
	}

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) {
	    TypeSystem ts = tc.typeSystem();

	    try {
	        Types.checkMissingParameters(this.subtype());
	    } catch (SemanticException e) {
	        Errors.issue(tc.job(), e, this.subtype());
	    }

	    try {
	        Types.checkMissingParameters(this.supertype());
	    } catch (SemanticException e) {
	        Errors.issue(tc.job(), e, this.supertype());
	    }

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
		return sub;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(sub, sup, ENTRY);
		v.visitCFG(sup, this, EXIT);
		return succs;
	}

	public String toString() {
		return sub + (equals ? " == " : " <: ") + sup;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		printBlock(sub, w, tr);
		w.write((equals ? " == " : " <: "));
		printBlock(sup, w, tr);
	}

}
