package x10.runtime;

public final class ValRail<T> extends AnyRail<T> {
    protected ValRail(Type type, int length) {
        super(type, length);
    }
    
    protected ValRail(Type type, int length, Object array) {
        super(type, length, array);
    }
    
    public void set(int i, T v) {
        ((T[]) value)[i] = v;
    }
}