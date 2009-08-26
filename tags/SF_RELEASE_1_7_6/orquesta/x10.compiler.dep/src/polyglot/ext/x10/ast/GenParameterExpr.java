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
import polyglot.ast.TypeNode;

/**
 * @author Christian Grothoff
 * 
 */
public interface GenParameterExpr extends Expr {
    List<TypeNode> args();
    GenParameterExpr args( List<TypeNode> args);
}
