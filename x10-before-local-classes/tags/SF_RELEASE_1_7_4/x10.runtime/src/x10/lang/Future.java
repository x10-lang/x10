/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 3, 2004
 */
package x10.lang;

/**
 * @author Christian Grothoff
 * @author Christoph von Praun
 */
public abstract class Future extends Object implements ValueType {

	/**
	 * Wait for the completion of this activity and return the
	 * return value.
	 */
	public abstract Object force();

	/**
	 * Return true if this activity has completed.
	 */
	public abstract boolean forced();

}
