/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;

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
