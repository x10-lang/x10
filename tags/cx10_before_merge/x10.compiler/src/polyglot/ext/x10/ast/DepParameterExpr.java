/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import java.util.List;

/**
 * @author vj Jan 9, 2005
 * 
 */
public interface DepParameterExpr extends Expr {
    Expr condition();
    List args();
    DepParameterExpr condition( Expr cond );
    DepParameterExpr args( List args);
    DepParameterExpr reconstruct( List args, Expr cond);
}
