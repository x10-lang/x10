package x10.array;

import x10.types.Type;

public class GenericArray<T> extends Array<T> {
    final Object backingArray;
    
    public GenericArray(Type<T> T, Dist d) {
        super(T, d);
        backingArray = T.makeArray(d.region.size());
    }

    @Override
    public T setOrdinal(T v, int rawIndex) {
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
        return new GenericArray<T>(T, d);
    }
}
