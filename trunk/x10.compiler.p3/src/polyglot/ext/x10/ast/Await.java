/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

/**
 * @author vj
 *
 * 
 */
public interface Await extends Stmt {
	Await expr( Expr e);
	Expr expr();

}
