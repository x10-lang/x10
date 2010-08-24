package polyglot.types;


public interface Ref<T> {
	public T get();
    public T getCached();
    public void update(T v);
    public boolean known();
}
