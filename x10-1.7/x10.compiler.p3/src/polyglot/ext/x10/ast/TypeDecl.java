/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.ClassMember;
import polyglot.ast.TopLevelDecl;
import polyglot.ext.x10.types.TypeDef;

public interface TypeDecl extends TopLevelDecl, ClassMember {
    TypeDef typeDef();
}