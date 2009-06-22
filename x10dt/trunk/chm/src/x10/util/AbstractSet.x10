/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of AbstractSet
 * 
 * @author Shane Markstrum
 * @javaauthor Josh Bloch
 * @javaauthor Neal Gafter
 * @version 08/02/06
 * @see java.util.AbstractSet
 */
 package x10.util;

import x10.lang.Object;

public abstract class AbstractSet extends AbstractCollection implements Set {
	protected AbstractSet() {}
	
    public boolean equals(nullable<Object> o) {
    	if (o == null) return false;
    	if (o == this)
    	    return true;

    	if (!(o instanceof Set))
    	    return false;
    	Collection c = (Collection) o;
    	if (c.size() != size())
    	    return false;
            try {
                return containsAll(c);
            } catch(ClassCastException unused)   {
                return false;
            } catch(NullPointerException unused) {
                return false;
            }
        }

    public int hashCode() {
    	int h = 0;
    	Iterator i = iterator();
    	int index = 0;
    	future<int>[] hashCodes = new future<int>[size()];
    	while (i.hasNext()) {
    	    final nullable<Object> obj = i.next();
                if (obj != null) {
                    final int index_f = index++;
                    hashCodes[index_f] = future (here) { obj.hashCode() };
                }
    	}
    	for (int j = 0; j < index; j++)
    		h += hashCodes[j].force();
    	return h;
    }

    public boolean removeAll(Collection c) {
        boolean modified = false;

        if (size() > c.size()) {
            for (Iterator i = c.iterator(); i.hasNext(); )
                modified |= remove(i.next());
        } else {
            for (Iterator i = iterator(); i.hasNext(); ) {
                if (c.contains((Object) i.next())) {
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

}
