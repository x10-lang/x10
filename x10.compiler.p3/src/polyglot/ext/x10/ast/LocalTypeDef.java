package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ext.x10.types.TypeDef;

public interface LocalTypeDef extends CompoundStmt {
    TypeDecl typeDef();
    LocalTypeDef typeDef(TypeDecl typeDef);
}
