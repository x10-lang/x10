/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.rtt.Equality;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class Box<T> extends Ref {
    protected final Type<T> type;
    protected final T value;
    
    public static class RTT<T> extends RuntimeType<Box<T>> {
        Type<T> type;

        public RTT(Type<?> type) {
            super(Box.class);
            this.type = (Type<T>) type;
        }
        
        @Override
        public boolean instanceof$(Object o) {
            return o instanceof Box && ((Box) o).type.isSubtype(type);
        }
        
        @Override
        public boolean isSubtype(Type<?> o) {
            if (o instanceof Box.RTT) {
                Box.RTT other = (Box.RTT) o;
                return type.isSubtype(other.type);
            }
            return super.isSubtype(o);
        }
    }
    
    protected Box(Type<?> type, T v) {
        assert v != null;
        this.value = v;
        this.type = (Type<T>) type;
    }
//    protected Box(Type<T> type, T v) {
//    	assert v != null;
//    	this.value = v;
//    	this.type = type;
//    }
    
    public static Box<Boolean> make(Type<Boolean> type, boolean v) { return new BoxedBoolean(v); }
    public static Box<Byte> make(Type<Byte> type, byte v) { return new BoxedByte(v); }
    public static Box<Short> make(Type<Short> type, short v) { return new BoxedShort(v); }
    public static Box<Character> make(Type<Character> type, char v) { return new BoxedChar(v); }
    public static Box<Integer> make(Type<Integer> type, int v) { return new BoxedInt(v); }
    public static Box<Long> make(Type<Long> type, long v) { return new BoxedLong(v); }
    public static Box<Float> make(Type<Float> type, float v) { return new BoxedFloat(v); }
    public static Box<Double> make(Type<Double> type, double v) { return new BoxedDouble(v); }

    public static <S> Ref make(Type<S> type, S v) {
        if (v == null)
            return null;
        if (v instanceof Box) {
            Box<?> box = (Box<?>) v;
            if (type.instanceof$(box.value())) {
                return make(type, (S) box.value());
            }
            else {
                throw new ClassCastException();
            }
        }
        if (v instanceof Ref)
            return (Ref) v;
        if (v instanceof Value)
        	return ((Value) v).box$();
        if (v instanceof String)
                return new BoxedString((String) v);
        if (v instanceof Boolean)
                return new BoxedBoolean((Boolean) v);
        if (v instanceof Byte)
                return new BoxedByte((Byte) v);
        if (v instanceof Character)
                return new BoxedChar((Character) v);
        if (v instanceof Short)
                return new BoxedShort((Short) v);
        if (v instanceof Integer)
                return new BoxedInt((Integer) v);
        if (v instanceof Long)
                return new BoxedLong((Long) v);
        if (v instanceof Float)
                return new BoxedFloat((Float) v);
        if (v instanceof Double)
                return new BoxedDouble((Double) v);

        // TOOD: Throwable

        return new Box<S>(type, v);
    }
    
    public T value() { return value; }
    
    public static <T> T unbox(Box<T> box) {
        if (box != null) {
            return box.value();
        }
        throw new ClassCastException();
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Box)
            return Equality.equalsequals(value, ((Box<?>) o).value);
        if (Equality.equalsequals(value, o))
            return true;
        return false;
    }
    
    public int hashCode() {
        return value.hashCode();
    }
    
    public String toString() {
        return value.toString();
    }
}
