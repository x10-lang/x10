/*
 * Created on Oct 28, 2004
 */
package x10.array;

/**
 * @author Christoph von Praun
 */
public class InvalidIndexException extends RuntimeException {
	/**
	 * Constructs a <code>InvalidIndexException</code> with no detail  message.
	 *
	 */
	public InvalidIndexException() {}

	/**
	 * Constructs a <code>InvalidIndexException</code> with the specified 
	 * detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public InvalidIndexException(String s) {
		super(s);
	}
}
