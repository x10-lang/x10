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

/**
 * An immutable representation of a prop(e1,..., en) statement in the body of a constructor.
 * @author vj
 *
 */
public interface AssignPropertyCall extends Stmt {
	List<Expr> args();
}
