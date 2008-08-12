package x10.constraint;

public abstract class XRef_c<T> {
    public T result;
    public final T get() {
	if (result == null)
	    result = compute();
	return result;
    }
    public abstract T compute();
    public String toString() {
	return String.valueOf(result);
    }
}
