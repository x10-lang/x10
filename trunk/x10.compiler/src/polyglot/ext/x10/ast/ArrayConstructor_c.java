/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Variable;
import polyglot.ext.jl.ast.Term_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/** An immutable representation of an X10 array constructor.
 * @author vj Dec 9, 2004
 * 
 */
public class ArrayConstructor_c extends Term_c implements
		ArrayConstructor {
	TypeNode base;
	Expr distribution;
	Variable formal;
	Block body;
	/**
	 * @param pos
	 */
	public ArrayConstructor_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}
	public ArrayConstructor_c(Position pos, TypeNode base, Expr distribution, 
			Variable formal, Block body) {
		super(pos);
		this.base = base;
		this.distribution = distribution;
		this.formal = formal;
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
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType()
	 */
	public TypeNode arrayBaseType() {
		return this.base;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region()
	 */
	public Expr distribution() {
		return this.distribution;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#body()
	 */
	public Block body() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#formal()
	 */
	public Variable formal() {
		return this.formal;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType(polyglot.ast.TypeNode)
	 */
	public ArrayConstructor arrayBaseType(TypeNode t) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region(polyglot.ast.Expr)
	 */
	public ArrayConstructor distribution(Expr e) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#body(polyglot.ast.Block)
	 */
	public ArrayConstructor body(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#formal(polyglot.ast.Variable)
	 */
	public ArrayConstructor formal(Variable v) {
		// TODO Auto-generated method stub
		return null;
	}

}
