package x10c.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.NewArray_c;
import polyglot.ast.TypeNode;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10CBackingArrayNewArray_c extends NewArray_c implements BackingArrayNewArray {
    public X10CBackingArrayNewArray_c(Position pos, TypeNode baseType, List<Expr> dims, int addDims, Type type) {
        super(pos, baseType, dims, addDims, null);
        this.type = type;
    }
}
