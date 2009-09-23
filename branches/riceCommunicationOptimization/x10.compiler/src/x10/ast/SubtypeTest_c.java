/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Feb 4, 2005
 *
 * 
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
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		return type(ts.Boolean());
	}

	public boolean isConstant() {
		return false;
	}

	public Object constantValue() {
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
	public List acceptCFG(CFGBuilder v, List succs) {
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
