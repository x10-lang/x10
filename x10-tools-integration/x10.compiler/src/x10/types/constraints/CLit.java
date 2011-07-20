package x10.types.constraints;

import java.util.Collections;
import java.util.List;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

import x10.constraint.XLit;


/**
 * An optimized representation of literals.
 * Keeps a value and a type.
 * @author vj
 */
public class CLit extends XLit implements Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    
    protected final Type type;
    
    public CLit(Object val, Type type) {
        super(val);
        this.type = Types.addSelfBinding(Types.baseType(type), this);
    }
    
    /**
     * Return the type of the literal.
     */
    public Type type() {
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CLit))
            return false;
        CLit other = (CLit) o;
        if (!super.equals(other))
            return false;
        if (type == null)
            return other.type == null;
        TypeSystem ts = type.typeSystem();
        return ts.typeEquals(Types.baseType(type), Types.baseType(other.type), ts.emptyContext());
    }

    @Override
    public String toString() {
        if (type != null && type.isUnsignedNumeric()) {
            return new IntLit_c(Position.COMPILER_GENERATED, getIntLitKind(type), ((Number) val).longValue()).toString();
        }
        return super.toString();
    }

    public static IntLit.Kind getIntLitKind(Type type) {
        if (type.isByte())   return IntLit.BYTE;
        if (type.isUByte())  return IntLit.UBYTE;
        if (type.isShort())  return IntLit.SHORT;
        if (type.isUShort()) return IntLit.USHORT;
        if (type.isInt())    return IntLit.INT;
        if (type.isUInt())   return IntLit.UINT;
        if (type.isLong())   return IntLit.LONG;
        if (type.isULong())  return IntLit.ULONG;
        return null;
    }
}
