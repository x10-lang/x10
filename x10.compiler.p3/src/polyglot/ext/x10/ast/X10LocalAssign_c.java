package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Assign.Operator;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.types.LocalInstance;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10LocalAssign_c extends LocalAssign_c {

    public X10LocalAssign_c(Position pos, Local left, Operator op, Expr right) {
        super(pos, left, op, right);
    }

    @Override
    public Type leftType() {
        LocalInstance li = local().localInstance();
        if (li == null)
            return null;
        if (li instanceof X10LocalInstance) {
            return ((X10LocalInstance) li).type();
        }
        return li.type();
    }
}