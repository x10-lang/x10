/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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
