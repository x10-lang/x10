package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ProcedureCall;
import polyglot.ast.TypeNode;

public interface X10ProcedureCall extends ProcedureCall {
    List<TypeNode> typeArguments();


}
