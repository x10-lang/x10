/*
 * Created on Oct 28, 2004
 */
package x10.array;

/**
 * @author Christoph von Praun
 */
public class PointOutOfRegionException extends RuntimeException {
	/**
	 * Constructs a <code>InvalidIndexException</code> with no detail  message.
	 *
	 */
	public PointOutOfRegionException() {}

	/**
	 * Constructs a <code>InvalidIndexException</code> with the specified 
	 * detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public PointOutOfRegionException(String s) {
		super(s);
	}
}
