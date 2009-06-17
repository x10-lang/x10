package x10.runtime;

public final class Rail<T> extends AnyRail<T> implements Settable<Integer,T> {
    protected Rail(Type type, int length) {
        super(type, length);
    }
    
    protected Rail(Type type, int length, Object array) {
        super(type, length, array);
    }
    
    public void set(Integer i, T v) {
        ((T[]) value)[i] = v;
    }
    
    public void set(int i, T v) {
        ((T[]) value)[i] = v;
    }
}
