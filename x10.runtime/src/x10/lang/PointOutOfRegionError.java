/*
 * Created on Oct 28, 2004
 */
package x10.lang;

/**
 * @author Christoph von Praun
 */
public class PointOutOfRegionError extends Error {
	/**
	 * Constructs a <code>InvalidIndexException</code> with no detail  message.
	 *
	 */
	public PointOutOfRegionError() {}

	/**
	 * Constructs a <code>InvalidIndexException</code> with the specified 
	 * detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public PointOutOfRegionError(String s) {
		super(s);
	}
}
