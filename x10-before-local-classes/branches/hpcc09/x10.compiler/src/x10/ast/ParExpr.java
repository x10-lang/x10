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

import polyglot.ast.Expr;

/** The immutable representation of the X10 expression ( Expr ).
 * The parentheses need to be propagated to the output; it is not correct for the parser
 * to just drop them.
 * @author vj Feb 4, 2005
 * 
 */
public interface ParExpr extends Expr {
	Expr expr();
	ParExpr expr(Expr expr);

}
