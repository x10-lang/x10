/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of AbstractCollection
 * 
 * @author Shane Markstrum
 * @javaauthor Josh Bloch
 * @javaauthor Neal Gafter
 * @version 08/02/06
 * @see java.util.AbstractCollection
 */
 package x10.util;

import x10.lang.Object;

public abstract class AbstractCollection extends x10.lang.Object implements Collection {
	
    protected AbstractCollection() {
    }

    // Query Operations

    public abstract Iterator iterator();

    public abstract int size();

    public boolean isEmpty() {
	return size() == 0;
    }

    public boolean contains(nullable<Object> o) {
	Iterator e = iterator();
	if (o==null) {
	    while (e.hasNext())
		if (e.next()==null)
		    return true;
	} else {
	    while (e.hasNext())
		if (o.equals(e.next()))
		    return true;
	}
	return false;
    }

    public nullable<Object>[.] toArray() {
	nullable<Object>[.] result = new nullable<Object>[[0:size()-1]->here];
	Iterator e = iterator();
	for (int i=0; e.hasNext(); i++)
	    result[i] = e.next();
	return result;
    }
 
    /**
    * @param a a reference to the array in which to store an
    *    array representation of the AbstractCollection.
    * @return a reference to a
    */
    
    public nullable<Object>[.] toArray(nullable<Object>[.] a) {
        int size = size();
        if (([0:size-1] && ((region(:self.rank==1)) (a.distribution|here).region)).size() < size)
            a = new nullable<Object>[[0:size-1]->here];
        
        Iterator it=iterator();
	nullable<Object>[.] result = a;
        for ( point p[i] : result.distribution|[0:size-1] )
            result[p] = it.next();
        if ((((region(:self.rank==1)) a.region) && [size:size]).size() != 0)
	    a[point.factory.point(size)] = null;
        return result;
    }

    // Modification Operations

     public boolean add(nullable<Object> o) {
	throw new UnsupportedOperationException();
    }

    public boolean remove(nullable<Object> o) {
	Iterator e = iterator();
	if (o==null) {
	    while (e.hasNext()) {
		if (e.next()==null) {
		    e.remove();
		    return true;
		}
	    }
	} else {
	    while (e.hasNext()) {
		if (o.equals(e.next())) {
		    e.remove();
		    return true;
		}
	    }
	}
	return false;
    }


    // Bulk Operations

    public boolean containsAll(Collection c) {
	Iterator e = c.iterator();
	while (e.hasNext())
	    if(!contains(e.next()))
		return false;
	return true;
    }

    public boolean addAll(Collection c) {
	boolean modified = false;
	Iterator e = c.iterator();
	while (e.hasNext()) {
	    if (add((Object) e.next()))
		modified = true;
	}
	return modified;
    }

    public boolean removeAll(Collection c) {
	boolean modified = false;
	Iterator e = iterator();
	while (e.hasNext()) {
	    if (c.contains((Object) e.next())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
    }

    public boolean retainAll(Collection c) {
	boolean modified = false;
	Iterator e = iterator();
	while (e.hasNext()) {
	    if (!c.contains((Object) e.next())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
    }

    public void clear() {
	Iterator e = iterator();
	while (e.hasNext()) {
	    e.next();
	    e.remove();
	}
    }


    //  String conversion

    public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("[");

        Iterator i = iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            Object o = (Object) i.next();
            buf.append(o == this ? "(this Collection)" : String.valueOf(o));
            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }

	buf.append("]");
	return buf.toString();
    }

}
