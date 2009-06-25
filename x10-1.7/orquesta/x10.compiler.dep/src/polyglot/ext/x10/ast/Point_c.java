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

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.TypeChecker;

/** An immutable representation of a point in a region. May be used in array access.
 * @author vj Dec 9, 2004
 * 
 */
public class Point_c extends Expr_c implements Point {

	List exprs;
	/**
	 * @param pos
	 */
	public Point_c(Position pos) {
		super(pos);
	}
	
	public Point_c( Position pos, List exprs ) {
		super(pos);
		this.exprs = TypedList.copyAndCheck(exprs, Expr.class, true);
	}

	@Override
	public Node typeCheck(TypeChecker tc) throws SemanticException {
	    return this.type(((X10TypeSystem) tc.typeSystem()).point());
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
	    return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		return succs;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Point#rank()
	 */
	public int rank() {
		if (exprs == null) return 0;
		return exprs.size();
	}

	public Expr valueAt(int i) {
		return (Expr) exprs.get(i);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Point#value(java.util.List)
	 */
	public Point value(List l) {
		Point_c n = (Point_c) this.copy();
		n.exprs = TypedList.copyAndCheck( l, Expr.class, true);
		return n;
	}
	public String toString() {
	    StringBuffer buff= new StringBuffer();
	    buff.append('[');
	    if (exprs != null) {
		for(Iterator iter= exprs.iterator(); iter.hasNext(); ) {
		    Expr coord= (Expr) iter.next();
		    buff.append(coord);
		    if (iter.hasNext())
			buff.append(", ");
		}
	    }
	    buff.append(']');
	    return buff.toString();
	}
}
