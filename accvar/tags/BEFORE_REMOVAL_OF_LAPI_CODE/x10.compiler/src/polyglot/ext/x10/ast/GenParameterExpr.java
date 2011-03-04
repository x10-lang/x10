package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import java.util.List;

/**
 * @author Christian Grothoff
 * 
 */
public interface GenParameterExpr extends Expr {
    List args();
    GenParameterExpr args( List args);
    GenParameterExpr reconstruct( List args);
}
