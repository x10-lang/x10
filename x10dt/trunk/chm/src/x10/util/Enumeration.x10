/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of Enumeration
 * 
 * @author Shane Markstrum
 * @javaauthor Lee Boynton
 * @version 08/02/06
 * @see java.util.Enumeration
 */
 package x10.util;

import x10.lang.Object;

public interface Enumeration {
	
	boolean hasMoreElements();
	nullable<Object> nextElement();
}
