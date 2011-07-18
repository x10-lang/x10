/**
 * 
 */
package x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Return;
import polyglot.ast.Stmt;

/**
 * @author vj
 *
 */
public interface Offer extends Stmt {
	
	  /** The expression to return. */
    Expr expr();
    /** Set the expression to return. */
    Offer expr(Expr expr);

}
