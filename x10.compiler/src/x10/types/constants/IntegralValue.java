/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.types.constraints.ConstraintManager;


/**
 * A class to represent that various kinds of Integral constants.
 */
public final class IntegralValue extends ConstantValue {
    private final long val;
    private final IntLit.Kind kind;
    
    IntegralValue(long v, IntLit.Kind k) {
        val = extend(v, k);
        kind = k;
    }
    
    private static long extend(long v, IntLit.Kind k) {
        switch (k) {
        case BYTE: return (long)(byte)v;
        case UBYTE: return ((long)(byte)v) & 0xFFL;
        case SHORT: return (long)(short)v;
        case USHORT: return ((long)(short)v) & 0xFFFFL;
        case INT: return (long)(int)v;
        case UINT: return ((long)(int)v) & 0xFFFFFFFFL;
        case LONG: return v;
        case ULONG: return v;
        default:
            throw new InternalCompilerError("Unknown kind of literal "+k);
        }
    }
    
    public byte byteValue() { 
        return (byte)val;
    }
    
    public short shortValue() {
        return (short)val;
    }
    
    public int intValue() {
        return (int)val;
    }
    
    public long longValue() {
        return val;
    }
    
    public IntLit.Kind kind() {
        return kind;
    }
    
    public boolean isByte() {
        return kind.equals(IntLit.Kind.BYTE);
    }
    
    public boolean isUByte() {
        return kind.equals(IntLit.Kind.UBYTE);
    }
    
    public boolean isShort() {
        return kind.equals(IntLit.Kind.SHORT);
    }
    
    public boolean isUShort() {
        return kind.equals(IntLit.Kind.USHORT);
    }
    
    public boolean isInt() {
        return kind.equals(IntLit.Kind.INT);
    }
    
    public boolean isUInt() {
        return kind.equals(IntLit.Kind.UINT);
    }
    
    public boolean isLong() {
        return kind.equals(IntLit.Kind.LONG);
    }
    
    public boolean isULong() {
        return kind.equals(IntLit.Kind.ULONG);
    }

    @Override
    public Object toJavaObject() {
        if (isByte() || isUByte()) {
            return Integer.valueOf((byte)val);
        } else if (isShort() || isUShort()) {
            return Integer.valueOf((short)val);
        } else if (isInt() || isUInt()) {
            return Integer.valueOf((int)val);
        } else {
            return Long.valueOf(val);
        }
    }
    
    @Override
    public IntLit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos) {
        type = Types.addSelfBinding(type, ConstraintManager.getConstraintSystem().makeLit(toJavaObject(), getLitType(ts)));
        return (IntLit)nf.IntLit(pos, kind, val).type(type);
    }

    @Override
    public Type getLitType(TypeSystem ts) {
        if (isULong()) {
            return ts.ULong();
        } else if (isLong()) {
            return ts.Long();
        } else if (isUInt()) {
            return ts.UInt();
        } else if (isInt()) {
            return ts.Int();
        } else if (isUShort()) {
            return ts.UShort();
        } else if (isShort()) {
            return ts.Short();
        } else if (isUByte()) {
            return ts.UByte();
        } else {
            return ts.Byte();
        }
    }

    @Override
    public IntLit toUntypedLit(NodeFactory nf, Position pos) {
        return nf.IntLit(pos, kind, val);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof IntegralValue) {
            IntegralValue iv = (IntegralValue)that;
            return kind.equals(iv.kind) && val == iv.val;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Long.valueOf(val).hashCode();
    }

    @Override
    public String toString() {
        return Long.toString(val);
    }
    
    @Override
    public long integralValue() {
        return (long)val;
    }
   
    @Override
    public double doubleValue() {
        return (double)val;
    }
    
    @Override
    public float floatValue() {
        return (float)val;
    }
}
