package x10.core;

import x10.types.Type;

public final class Rail<T> extends AnyRail<T> implements Settable<Integer,T> {
    public Rail(Type<T> type, int length) {
        super(type, length);
    }
    
    public Rail(Type<T> type, int length, Object array) {
        super(type, length, array);
    }
    
    public T set(T v, Integer i) {
        return type.setArray(value, i, v);
    }
}
