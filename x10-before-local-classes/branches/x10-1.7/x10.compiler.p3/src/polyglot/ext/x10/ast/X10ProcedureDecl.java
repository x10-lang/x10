/*
 *
 * (C) Copyright IBM Corporation 2009.
 *
 *  This file is part of X10 Language.
 */

package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ProcedureDecl;

public interface X10ProcedureDecl extends ProcedureDecl {
    DepParameterExpr guard();

    List<TypeParamNode> typeParameters();
}
