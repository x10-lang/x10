/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of Iterator
 * 
 * @author Shane Markstrum
 * @javaauthor Josh Bloch
 * @version 08/02/06
 * @see java.util.Iterator
 */
 package x10.util;

import x10.lang.Object;

public interface Iterator {
	
	boolean hasNext();
    nullable<Object> next();
	void remove();
}
