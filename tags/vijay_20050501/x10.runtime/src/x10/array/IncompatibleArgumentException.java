/*
 * Created on Oct 28, 2004
 */
package x10.array;

/**
 * This excepetion is thrown if the array type argument of a method is not 
 * compatible in the shape with the target array. 
 * 
 * @author Christoph von Praun
 */
public class IncompatibleArgumentException extends RuntimeException {
	/**
	 * Constructs a <code>IncompatibleArgumentException</code> with no detail  message.
	 *
	 */
	public IncompatibleArgumentException() {}

	/**
	 * Constructs a <code>IncompatibleArgumentException</code> with the specified 
	 * detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public IncompatibleArgumentException(String s) {
		super(s);
	}
}
