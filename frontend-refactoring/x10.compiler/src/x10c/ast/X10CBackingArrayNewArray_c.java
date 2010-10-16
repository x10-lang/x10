package x10c.ast;

import java.util.List;

import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.Expr;
import x10.ast.NewArray_c;
import x10.ast.TypeNode;

public class X10CBackingArrayNewArray_c extends NewArray_c implements BackingArrayNewArray {
    public X10CBackingArrayNewArray_c(Position pos, TypeNode baseType, List<Expr> dims, int addDims, Type type) {
        super(pos, baseType, dims, addDims, null);
        this.type = type;
    }
}
