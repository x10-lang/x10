package x10.core;

import x10.types.Type;

public final class Rail<T> extends AnyRail<T> implements Settable<Integer,T> {
    public Rail(Type<T> type, int length) {
        super(type, length);
    }
    
    public Rail(Type<T> type, int length, Object array) {
        super(type, length, array);
    }
    
    public void set(Integer i, T v) {
        type.setArray(value, i, v);
    }
}
