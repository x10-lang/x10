package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

import java.util.List;

/**
 * 
 */
public interface Drop extends Stmt
{
    /**
     * 
     */
    List clocks();

    /**
     *.
     */
    Drop clocks(List clocks);

    public Drop append(Expr clock);
}
