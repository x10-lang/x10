/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import java.util.List;

/** The AST node representing the X10 construct when (c) {S} else (c) {S} ...
 * @author ??
 */
public interface When extends Stmt {
    /**
     *
     */
    List statements();

    /**
     * 
     */
    When statements(List statements);

    /**
     * 
     */
    List exprs();

    public When exprs(List exprs);

    public When append(Expr expr);

    public When append(Stmt stmt);
}
