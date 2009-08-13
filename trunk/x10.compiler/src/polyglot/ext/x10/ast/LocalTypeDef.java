/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ext.x10.types.TypeDef;

public interface LocalTypeDef extends CompoundStmt {
    TypeDecl typeDef();
    LocalTypeDef typeDef(TypeDecl typeDef);
}
