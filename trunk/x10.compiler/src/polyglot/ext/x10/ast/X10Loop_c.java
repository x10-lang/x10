/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Variable;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/** Captures the commonality of for, foreach and ateach loops in X10.
 * TODO:
 * (1) formal must be a variable whose type will be that of the region underlying domain.
 * (2) domain must be an array, distribution or region. If it is an array or distribution a, 
 *     the system must behave as if the user typed a.region.
 * (3) Perhaps we can allow continue statements within a for loop, but not within 
 *     a foreach or an ateach.
 * @author vj Dec 9, 2004
 * 
 */
public abstract class X10Loop_c extends Stmt_c implements X10Loop {
		Variable formal;
		Expr domain;
		Stmt body;

	/**
	 * @param pos
	 */
	protected X10Loop_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}

	protected X10Loop_c( Position pos, Variable formal, Expr domain, Stmt body) {
		super( pos );
		this.formal = formal;
		this.domain = domain;
		this.body = body;
		
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
	 * @see polyglot.ext.x10.ast.X10Loop#body()
	 */
	public Stmt body() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#formal()
	 */
	public Variable formal() {
		return this.formal;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#domain()
	 */
	public Expr domain() {
		return this.domain;
	}

}
