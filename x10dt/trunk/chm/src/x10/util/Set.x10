/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of Set
 * 
 *
 * @author Shane Markstrum
 * @javaauthor Josh Bloch
 * @javaauthor Neal Grafter
 * @version 08/02/06
 * @see java.util.Set
 */
 package x10.util;

import x10.lang.Object;

public interface Set extends Collection{
	
	int size();
	boolean isEmpty();
	boolean contains(nullable<Object> o);
	Iterator iterator();
	nullable<Object>[.] toArray();
    nullable<Object>[.] toArray(nullable<Object>[.] a);
    boolean add(nullable<Object> o);
	boolean remove(nullable<Object> o);
	boolean containsAll(Collection c);
	boolean addAll(Collection c);
	boolean retainAll(Collection c);
	boolean removeAll(Collection c);
	void clear();
	boolean equals(nullable<Object> o);
	int hashCode();
	
}
