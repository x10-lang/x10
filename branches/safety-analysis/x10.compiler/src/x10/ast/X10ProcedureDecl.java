package x10.ast;

import java.util.List;

import polyglot.ast.ProcedureDecl;

public interface X10ProcedureDecl extends ProcedureDecl {
    DepParameterExpr guard();

    List<TypeParamNode> typeParameters();
}
