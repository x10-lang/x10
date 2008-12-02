/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.Type;

public interface X10CastInfo {
    public boolean notNullRequired();

    public boolean isDepTypeCheckingNeeded();

    public Type type();

    public boolean isToTypeNullable();

    public TypeNode getTypeNode();

    public Expr expr();
}
