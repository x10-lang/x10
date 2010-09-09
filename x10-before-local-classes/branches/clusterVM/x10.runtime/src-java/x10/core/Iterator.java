package x10.core;

/** Just like java.util.Iterator, but without remove(). */
public interface Iterator<T> {
	public boolean hasNext();
	public T next();
}
