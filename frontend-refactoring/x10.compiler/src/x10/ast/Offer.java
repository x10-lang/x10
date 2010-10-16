/**
 * 
 */
package x10.ast;


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
