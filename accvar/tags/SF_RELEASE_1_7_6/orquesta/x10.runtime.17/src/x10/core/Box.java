package x10.core;

import x10.types.Type;
import x10.types.Types;

public class Box<T> extends Ref {
    Type<T> type;
    T value;
    
    private Box(Type<T> type, T v) {
        assert v != null;
        this.value = v;
        this.type = type;
    }
    
    public static Box<java.lang.Boolean> make(boolean v) { return new Box<Boolean>(Types.BOOLEAN, v); }
    public static Box<java.lang.Byte> make(byte v) { return new Box<Byte>(Types.BYTE, v); }
    public static Box<java.lang.Short> make(short v) { return new Box<Short>(Types.SHORT, v); }
    public static Box<java.lang.Character> make(char v) { return new Box<Character>(Types.CHAR, v); }
    public static Box<java.lang.Integer> make(int v) { return new Box<Integer>(Types.INT, v); }
    public static Box<java.lang.Long> make(long v) { return new Box<Long>(Types.LONG, v); }
    public static Box<java.lang.Float> make(float v) { return new Box<Float>(Types.FLOAT, v); }
    public static Box<java.lang.Double> make(double v) { return new Box<Double>(Types.DOUBLE, v); }
    
    public static <S> Ref make(S v) {
        if (v == null)
            return null;
        if (v instanceof Ref)
            return (Ref) v;
        return new Box<S>(Types./*<S>*/runtimeType(v.getClass()), v);
    }
    
    public Object value() { return value; }

    public boolean equals(Object o) {
        if (o instanceof Box) {
            return value.equals(((Box<?>) o).value);
        }
        return false;
    }
    
    public int hashCode() {
        return value.hashCode();
    }
    
    public String toString() {
        return value.toString();
    }
}
