package polyglot.ext.x10.ast;

import polyglot.ast.ClassMember;
import polyglot.ast.TopLevelDecl;
import polyglot.ext.x10.types.TypeDef;

public interface TypeDecl extends TopLevelDecl, ClassMember {
    TypeDef typeDef();
}
