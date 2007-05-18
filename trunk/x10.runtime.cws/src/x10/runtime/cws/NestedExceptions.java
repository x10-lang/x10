/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.cws;

import java.util.List;
/**
 * Nested exceptions intended to be thrown at a sync point.
 * @author vj 04/18/07
 *
 */
public class NestedExceptions extends Exception {
	public List<Exception> exceptions;
	public NestedExceptions(List<Exception> e) {
		super();
		exceptions = e;
	}
	public String toString() {
		return "NestedException<" + (exceptions.size() > 0 ? exceptions.get(1) : "null") + ">"; 
	}

}
