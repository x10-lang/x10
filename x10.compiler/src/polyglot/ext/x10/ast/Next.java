package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

import java.util.List;

/** The node constructed for the X10 construct (c1,...,cn).next();
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
