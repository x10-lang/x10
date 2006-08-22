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
