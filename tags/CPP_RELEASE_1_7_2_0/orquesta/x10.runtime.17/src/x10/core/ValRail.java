package x10.core;

import x10.types.Type;

public final class ValRail<T> extends AnyRail<T> {
    public ValRail(Type<T> type, int length) {
        super(type, length);
    }
    
    public ValRail(Type<T> type, int length, Object array) {
        super(type, length, array);
    }
}