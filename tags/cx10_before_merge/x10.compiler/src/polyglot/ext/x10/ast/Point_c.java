/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.util.TypedList;

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


	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term entry() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Point#rank()
	 */
	public int rank() {
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

}
