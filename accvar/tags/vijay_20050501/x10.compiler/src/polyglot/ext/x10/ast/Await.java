/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Stmt;
import polyglot.ast.Expr;

/**
 * @author vj
 *
 * 
 */
public interface Await extends Stmt {
	Await expr( Expr e);

}
