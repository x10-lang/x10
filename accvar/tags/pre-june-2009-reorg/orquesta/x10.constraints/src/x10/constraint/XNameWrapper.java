/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public class XNameWrapper<T> implements XName {
	T v;
	String s;

	public XNameWrapper(T v) {
		this(v, v.toString());
	}
	
	public XNameWrapper(T v, String s) {
		this.v = v;
		this.s = s;
	}

	public T val() {
		return v;
	}
	
	public String toString() {
		return s;
	}

	public int hashCode() {
		return v.hashCode();
	}

	public boolean equals(Object o) {
		return o instanceof XNameWrapper && v.equals(((XNameWrapper<?>) o).v);
	}
}
