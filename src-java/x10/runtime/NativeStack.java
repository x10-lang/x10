/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

public class NativeStack<T> extends java.util.Stack<T> {
	// HACK discard type parameter
	public NativeStack(Object typeT) { super(); }
}
