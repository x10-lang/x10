/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;


/** The immutable representation of an X10 region. This AST node is created
 * to store the information in the X10 construct [range1,...,rangek], as used,
 * e.g. in the RHS of a region declaration: region R = [range1,...,rangek].
 * If we run into difficulties with such syntax for regions we can use a 
 * new region(range1,...,rangek) syntax.
 * TODO:
 * (1) Compile-time error if each expr in exprs is not of type range.
 * (2) Compile-time error if a range can be established to be empty?
 * (3) Compile-time error if k = 0?
 * (4) Need there be an upper bound on the rank? (No.)
 * 
 * @author vj Dec 9, 2004
 * 
 */
public class Region_c extends Expr_c implements Region {
	List<Expr> exprs;
	
	/**
	 * @param pos
	 */
	public Region_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}

	public Region_c( Position pos, List exprs) {
		super(pos);
		this.exprs = TypedList.copyAndCheck( exprs, Expr.class, true);
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		// TODO Auto-generated method stub
		return succs;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Region#rank()
	 */
	public int rank() {
		// TODO Auto-generated method stub
		return exprs.size();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Region#index(int)
	 */
	public Expr index(int i) {
		// TODO Auto-generated method stub
		return (Expr) exprs.get(i);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Region#ranges()
	 */
	public List<Expr> ranges() {
		// TODO Auto-generated method stub
		return exprs;
	}

}
