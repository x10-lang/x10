package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

import java.util.List;

/**
 *
 */
public interface Next extends Stmt
{
    /**
     * 
     */
    List clocks();

    /**
     *
     */
    Next clocks(List clocks);

    public Next append(Expr clock);
}
