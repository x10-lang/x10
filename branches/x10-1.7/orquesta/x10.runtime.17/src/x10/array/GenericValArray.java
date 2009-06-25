package x10.array;

import x10.types.Type;

public class GenericValArray<T> extends ValArray<T> {
    final Object backingArray;
    
    public GenericValArray(Type<T> T, Dist d) {
        super(T, d);
        backingArray = T.makeArray(d.region.size());
    }

    @Override
    protected T setOrdinal(T v, int rawIndex) {
        T.setArray(backingArray, rawIndex, v);
        return v;
    }

    @Override
    public Object getBackingArray() {
        return backingArray;
    }

    @Override
    public T getOrdinal(int rawIndex) {
        return T.getArray(backingArray, rawIndex);
    }

    @Override
    protected BaseAnyArray<T> newInstance(Dist d) {
        return new GenericValArray<T>(T, d);
    }
}
