/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import polyglot.ast.CanonicalTypeNode;

public interface X10CanonicalTypeNode extends CanonicalTypeNode {
    public DepParameterExpr constraintExpr();
    public X10CanonicalTypeNode constraintExpr(DepParameterExpr e);
}
