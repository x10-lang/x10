/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import java.util.List;

/**
 * @author
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
