/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

import java.util.*;

/** Collection utilities. */
public class CollectionUtil
{
        public static List<String> dummyStringList(int length) {
            if (length == 0) return Collections.<String>emptyList();
            if (length == 1) return Collections.singletonList("a1");
            List<String> list = new ArrayList<String>(length);
            for (int i = 0; i < length; i++) {
        	list.add("a" + (i+1));
            }
            return list;
        }

	/** Append <code>o</code> to <code>l</code>, returning <code>l</code>. */
	public static <T> List<T> add(List<T> l, T o) {
		l.add(o);
		return l;
	}

	/**
	 * Return true if the predicate is true for each pair of elements from <code>a</code> and <code>b</code>.
	 */
	public static <T> boolean allElementwise(Collection<? extends T> a, Collection<? extends T> b, Predicate2<T> predicate) {
	    if (a == b) {
	        return true;
	    }

	    // the case where both are null is handled in the previous if.
	    if (a == null ^ b == null) {
	        return false;
	    }

	    Iterator<? extends T> i = a.iterator();
	    Iterator<? extends T> j = b.iterator();

	    while (i.hasNext() && j.hasNext()) {
	        T o = i.next();
	        T p = j.next();

	        if (! predicate.isTrue(o, p)) {
	            return false;
	        }
	    }

	    if (i.hasNext() || j.hasNext()) {
	        return false;
	    }

	    return true;
	}

	/**
	 * Return true if <code>a</code> and <code>b</code> are
	 * pointer equal, or if iterators over both return the same
	 * sequence of pointer equal elements.
	 */
	public static <T> boolean allEqual(Collection<? extends T> a, Collection<? extends T> b) {
		if (a == b) {
			return true;
		}

		// the case where both are null is handled in the previous if.
		if (a == null ^ b == null) {
			return false;
		}

		Iterator<? extends T> i = a.iterator();
		Iterator<? extends T> j = b.iterator();

		while (i.hasNext() && j.hasNext()) {
			T o = i.next();
			T p = j.next();

			if (o != p) {
				return false;
			}
		}

		if (i.hasNext() || j.hasNext()) {
			return false;
		}

		return true;
	}

	public static <T> List<T> append(Collection<? extends T> l1, Collection<? extends T> l2) {
		List<T> l = new ArrayList<T>();
		l.addAll(l1);
		l.addAll(l2);
		return l;
	}

	/** Return an empty list. */
	public static <T> List<T> list() {
		return Collections.<T>emptyList();
	}

	/** Return a singleton list containing <code>o</code>. */
	public static <T> List<T> list(T o) {
		return Collections.<T>singletonList(o);
	}

	/** Return a list containing <code>o1</code> and <code>o2</code>. */
	public static <T> List<T> list(T o1, T o2, T... o3s) {
		List<T> l = new ArrayList<T>(2+o3s.length);
		l.add(o1);
		l.add(o2);
		for (T o3 : o3s) {
		    l.add(o3);
		}
		return l;
	}

        public static <T> T firstOrElse(Collection<? extends T> l, T alt) {
                Iterator<? extends T> i = l.iterator();
                if (i.hasNext()) return i.next();
                return alt;
        }


        public static <T> Iterator<Object[]> pairs(Collection<T> l) {
            if (l.size() == 0) return Collections.<Object[]>emptyList().iterator();
                List<Object[]> x = new ArrayList<Object[]>(l.size()-1);
                Object prev = null;
                for (T curr : l) {
                    if (prev != null) x.add(new Object[] { prev, curr });
                    prev = curr;
                }
                return x.iterator();
        }

	/**
	 * Apply <code>t</code> to each element of <code>l</code>.
	 * <code>l</code> is not modified.
	 * @return A list containing the result of each transformation,
	 * in the same order as the original elements.
	 */
	public static <S,T> List<T> map(List<S> l, Transformation<S,T> t) {
		List<T> m = new ArrayList<T>(l.size());
		for (Iterator<T> i = new TransformingIterator<S,T>(l.iterator(), t);
			i.hasNext(); )
		{
			m.add(i.next());
		}
		return m;
	}

	/**
	 * Return an empty non-null list if the argument list is null.
	 *
	 * @param l a possibly null list
	 * @return a non-null list
	 */
	public static <T> List<T> nonNullList(List<T> l) {
		if (l != null)
			return l;
		return Collections.<T>emptyList();
	}

	public static String listToString(Collection<?> l) {
		StringBuffer sb = new StringBuffer();

		for (Iterator<?> i = l.iterator(); i.hasNext(); ) {
			Object o = i.next();
			if (o == null)
				sb.append("null");
			else
				sb.append(o.toString());

			if (i.hasNext()) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}
}
