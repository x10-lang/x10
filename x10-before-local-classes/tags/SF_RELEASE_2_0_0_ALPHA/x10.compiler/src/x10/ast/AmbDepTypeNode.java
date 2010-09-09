/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;

public interface AmbDepTypeNode extends TypeNode, Ambiguous {
    TypeNode base();
    AmbDepTypeNode base(TypeNode base);
    
    DepParameterExpr constraint();
    AmbDepTypeNode constraint(DepParameterExpr dep);
}
