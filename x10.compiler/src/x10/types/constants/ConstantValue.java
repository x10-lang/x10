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
import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.Closure_c;

/**
 * Simple class hierarchy to represent constant values.
 */
public abstract class ConstantValue {
    
    public abstract Lit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos);

    public abstract Lit toUntypedLit(NodeFactory nf, Position pos);
    
    public abstract Type getLitType(TypeSystem ts);

    public abstract Object toJavaObject();
    
    public static Object toJavaObject(ConstantValue v) {
        if (v == null || v instanceof ClosureValue) {
            return null;
        } else {
            return v.toJavaObject();
        }
    }

    public static NullValue makeNull() {
        return new NullValue();
    }
    
    public static BooleanValue makeBoolean(boolean v) {
        return new BooleanValue(v);
    }
    
    public static CharValue makeChar(char c) {
        return new CharValue(c);
    }
    
    public static IntegralValue makeByte(byte v) {
        return new IntegralValue(v, IntLit.Kind.BYTE);
    }
    
    public static IntegralValue makeUByte(byte v) {
        return new IntegralValue(v, IntLit.Kind.UBYTE);
    }
    
    public static IntegralValue makeShort(short v) {
        return new IntegralValue(v, IntLit.Kind.SHORT);
    }
    
    public static IntegralValue makeUShort(short v) {
        return new IntegralValue(v, IntLit.Kind.USHORT);
    }
    
    public static IntegralValue makeInt(int v) {
        return new IntegralValue(v, IntLit.Kind.INT);
    }
    
    public static IntegralValue makeUInt(int v) {
        return new IntegralValue(v, IntLit.Kind.UINT);
    }
    
    public static IntegralValue makeLong(long v) {
        return new IntegralValue(v, IntLit.Kind.LONG);
    }
    
    public static IntegralValue makeULong(long v) {
        return new IntegralValue(v, IntLit.Kind.ULONG);
    }
    
    public static IntegralValue makeIntegral(long v, IntLit.Kind k) {
        return new IntegralValue(v, k);
    }
    
    public static FloatValue makeFloat(float f) {
        return new FloatValue(f);
    }
    
    public static DoubleValue makeDouble(double d) {
        return new DoubleValue(d);
    }
    
    public static StringValue makeString(String v) {
        return new StringValue(v);
    }
    
    public static ClosureValue makeClosure(Closure_c cls) {
        return new ClosureValue(cls);
    }
    
    public static ConstantValue make(Type type, double v) {
        if (type.isDouble()) return ConstantValue.makeDouble(v);
        if (type.isFloat()) return ConstantValue.makeFloat((float) v);
        if (type.isLong()) return  ConstantValue.makeLong((long) v);
        if (type.isULong()) return ConstantValue.makeULong((long)v);
        if (type.isInt()) return ConstantValue.makeInt((int) v);
        if (type.isUInt()) return ConstantValue.makeUInt((int) v);
        if (type.isChar()) return ConstantValue.makeChar((char) v);
        if (type.isShort()) return ConstantValue.makeShort((short) v);
        if (type.isUShort()) return ConstantValue.makeUShort((short) v);
        if (type.isByte()) return ConstantValue.makeByte((byte) v);
        if (type.isUByte()) return ConstantValue.makeUByte((byte) v);

        throw new InternalCompilerError("Unexpected type "+type);
    }
 
    public static ConstantValue make(Type type, float v) {
        if (type.isDouble()) return ConstantValue.makeDouble((double)v);
        if (type.isFloat()) return ConstantValue.makeFloat((float) v);
        if (type.isLong()) return  ConstantValue.makeLong((long) v);
        if (type.isULong()) return ConstantValue.makeULong((long)v);
        if (type.isInt()) return ConstantValue.makeInt((int) v);
        if (type.isUInt()) return ConstantValue.makeUInt((int) v);
        if (type.isChar()) return ConstantValue.makeChar((char) v);
        if (type.isShort()) return ConstantValue.makeShort((short) v);
        if (type.isUShort()) return ConstantValue.makeUShort((short) v);
        if (type.isByte()) return ConstantValue.makeByte((byte) v);
        if (type.isUByte()) return ConstantValue.makeUByte((byte) v);

        throw new InternalCompilerError("Unexpected type "+type);
    }

    public static ConstantValue make(Type type, long v) {
        if (type.isDouble()) return ConstantValue.makeDouble((double)v);
        if (type.isFloat()) return ConstantValue.makeFloat((float) v);
        if (type.isLong()) return  ConstantValue.makeLong((long) v);
        if (type.isULong()) return ConstantValue.makeULong((long)v);
        if (type.isInt()) return ConstantValue.makeInt((int) v);
        if (type.isUInt()) return ConstantValue.makeUInt((int) v);
        if (type.isChar()) return ConstantValue.makeChar((char) v);
        if (type.isShort()) return ConstantValue.makeShort((short) v);
        if (type.isUShort()) return ConstantValue.makeUShort((short) v);
        if (type.isByte()) return ConstantValue.makeByte((byte) v);
        if (type.isUByte()) return ConstantValue.makeUByte((byte) v);

        throw new InternalCompilerError("Unexpected type "+type);
    }
    
    public static ConstantValue make(Type type, Object v) {
        if (v instanceof ConstantValue) {
            return (ConstantValue)v;
        }
        
        if (null == v && type.isReference()) {
            return ConstantValue.makeNull();
        }
        
        if (type.isBoolean()) return ConstantValue.makeBoolean(((Boolean)v).booleanValue());
        if (type.isDouble()) return ConstantValue.makeDouble(((Number)v).doubleValue());
        if (type.isFloat()) return ConstantValue.makeFloat(((Number)v).floatValue());
        if (type.isLong()) return  ConstantValue.makeLong((long) ((Number)v).longValue());
        if (type.isULong()) return ConstantValue.makeULong((long) ((Number)v).longValue());
        if (type.isInt()) return ConstantValue.makeInt((int) ((Number)v).longValue());
        if (type.isUInt()) return ConstantValue.makeUInt((int) ((Number)v).longValue());
        if (type.isChar()) return ConstantValue.makeChar((char) ((Number)v).longValue());
        if (type.isShort()) return ConstantValue.makeShort((short) ((Number)v).longValue());
        if (type.isUShort()) return ConstantValue.makeUShort((short) ((Number)v).longValue());
        if (type.isByte()) return ConstantValue.makeByte((byte) ((Number)v).longValue());
        if (type.isUByte()) return ConstantValue.makeUByte((byte) ((Number)v).longValue());
        if (type.isSubtype(type.typeSystem().String(), type.typeSystem().emptyContext())) {
            return ConstantValue.makeString((String)v);
        }

        throw new InternalCompilerError("Unexpected type "+type+" v = "+v+" (of type "+v.getClass()+")");
    }

    
    public long integralValue() {
        throw new InternalCompilerError("This constant value does not have an integral value "+this);
    }
   
    public double doubleValue() {
        throw new InternalCompilerError("This constant value does not have an double value "+this);
    }
    
    public float floatValue() {
        throw new InternalCompilerError("This constant value does not have an float value "+this);
    }
    
    public boolean isNull() {
        return false;
    }
}
