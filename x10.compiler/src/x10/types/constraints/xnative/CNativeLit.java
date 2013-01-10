package x10.types.constraints.xnative;

import polyglot.ast.IntLit_c;
import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.constraint.xnative.XNativeLit;
import x10.types.constraints.ConstraintManager;

/**
 * An optimized representation of literals.
 * Keeps a value and a type.
 * @author vj
 */
public class CNativeLit extends XNativeLit<Type, Object> implements Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    public CNativeLit(Object val, Type type) {
        super(val, type);
    	//super(val, Types.baseType(type));
        //[DC] at some point should add this
        //this.type = Types.addSelfBinding(Types.baseType(type), this);
    }

    @Override 
    public String toString() {
    	/*
        if (type().isUnsignedNumeric()) {
            return new IntLit_c(Position.COMPILER_GENERATED, 
            					ConstraintManager.getConstraintSystem().getIntLitKind(type()), 
            					((Number) val).longValue()).toString();
        }
        */
        return super.toString();
    }

}
