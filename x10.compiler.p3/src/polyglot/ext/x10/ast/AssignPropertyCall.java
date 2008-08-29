/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.util.TypedList;

/**
 * An immutable representation of a prop(e1,..., en) statement in the body of a constructor.
 * @author vj
 *
 */
public interface AssignPropertyCall extends Stmt {
	/** Return a copy of this node with this.expr equal to the given expr.
	   * @see polyglot.ext.x10.ast.Await#expr(polyglot.ast.Expr)
	   */
	public AssignPropertyCall args(List<Expr> args);
	public List<Expr> args();
	
	/** Return a copy of this node with this.expr equal to the given expr.
	 * @see polyglot.ext.x10.ast.Await#expr(polyglot.ast.Expr)
	 */
	public AssignPropertyCall typeArgs(List<TypeNode> args);
	public List<TypeNode> typeArgs();
}
