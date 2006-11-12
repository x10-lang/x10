package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;

/**
 * @author Christian Grothoff
 * 
 */
public interface GenParameterExpr extends Expr {
    List args();
    GenParameterExpr args( List args);
    GenParameterExpr reconstruct( List args);
}
