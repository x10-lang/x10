/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import java.util.List;

/** The AST node representing the X10 construct when (c) {S} else (c) {S} ...
 * @author vj
 */
public interface When extends Stmt {
	public interface Branch {
	  Branch branch (Expr cond, Stmt stmt);
	  Expr expr();
	  Stmt stmt();
	}
    
    /** Add this branch to the list. To be called only by the parser.
     * @param b
     */
    void add(Branch b);

    /** Return a new When statement with the given branches.
     * 
     */
    When branches(Expr expr, Stmt stmt, List rest);

   
}
