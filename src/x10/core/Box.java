package x10.core;

import x10.types.Equality;
import x10.types.RuntimeType;
import x10.types.Type;
import x10.types.Types;

public class Box<T> extends Ref {
    Type<T> type;
    T value;
    
    public static class RTT extends RuntimeType<Box<?>> {
        Type<?> type;

        public RTT(Type<?> type) {
            super(Box.class);
            this.type = type;
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
    
    private Box(Type<T> type, T v) {
        assert v != null;
        this.value = v;
        this.type = type;
    }
    
    public static Box<Boolean> make(Type<Boolean> type, boolean v) { return new Box<Boolean>(type, v); }
    public static Box<Byte> make(Type<Byte> type, byte v) { return new Box<Byte>(type, v); }
    public static Box<Short> make(Type<Short> type, short v) { return new Box<Short>(type, v); }
    public static Box<Character> make(Type<Character> type, char v) { return new Box<Character>(type, v); }
    public static Box<Integer> make(Type<Integer> type, int v) { return new Box<Integer>(type, v); }
    public static Box<Long> make(Type<Long> type, long v) { return new Box<Long>(type, v); }
    public static Box<Float> make(Type<Float> type, float v) { return new Box<Float>(type, v); }
    public static Box<Double> make(Type<Double> type, double v) { return new Box<Double>(type, v); }

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
