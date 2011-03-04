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

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.Ref;

/**
 * @author vj Jan 9, 2005
 * 
 */
public interface DepParameterExpr extends Ambiguous {
    Ref<? extends Constraint> constraint();
    Expr condition();
    List<Formal> formals();
    List<Expr> args();
    DepParameterExpr condition( Expr cond );
    DepParameterExpr formals( List<Formal> formals);
    DepParameterExpr args( List<Expr> args);
    DepParameterExpr reconstruct( List<Formal> formals, List<Expr> args, Expr cond);
}
