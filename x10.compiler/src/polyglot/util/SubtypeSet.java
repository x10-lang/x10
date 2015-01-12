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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;

import java.util.*;

import polyglot.types.*;

/**
 * Class to implement sets containing <code>polyglot.types.Type </code>.  
 * Set membership is based on the subtype relationships.  Thus, if 
 * <code>S</code> is a supertype of <code>A</code> and <code>B</code>, then
 * { <code>S</code> } union { <code>A</code>,<code>B</code> } = 
 * { <code>S</code> }.  Similarily, we remove elements from the set such 
 * that if <code>s</code> is an element of a set <code>S</code>, then a
 * call to remove <code>r</code> removes all <code>s</code> s.t. r is a 
 * a supertype of s.
 */
public class SubtypeSet implements java.util.Set<Type>
{
    protected List<Type> v; 
    protected TypeSystem ts;
    protected Type topType;  // Everything in the set must be a subtype of topType.

    /**
     * Creates an empty SubtypeSet
     */
    public SubtypeSet(TypeSystem ts) {
	this(ts.Any());
    }

    public SubtypeSet(Type top) {
	v = new ArrayList<Type>();
        this.ts = top.typeSystem();
	this.topType = top;
    }

    /**
     * Creates a copy of the given SubtypeSet
     */
    public SubtypeSet(SubtypeSet s) {
      v = new ArrayList<Type>(s.v);
      ts = s.ts;
      topType = s.topType;
    }

    public SubtypeSet(TypeSystem ts, Collection<Type> c) {
      this(ts);
      addAll(c);
    }

    public SubtypeSet(Type top, Collection<Type> c) {
      this(top);
      addAll(c);
    }

    /**
     * Add an element of type <code>polyglot.types.Type</code> to the set
     * only if it has no supertypes already in the set. If we do add it, 
     * remove any subtypes of <code>o</code>
     * 
     * @param o The element to add.
     */
    public boolean add(Type o) {
        if (o == null) {
	    return false;
	}

        Type type = o;

        Context context = ts.emptyContext();
        
        if (ts.isSubtype(type, topType, context)) {
            boolean haveToAdd = true;

            for (Iterator<Type> i = v.iterator(); i.hasNext(); ) {
                Type t = i.next();

                if (! ts.typeEquals(t, type, context) && ts.isSubtype(t, type, context)) {
                    i.remove();
                }

                if (ts.isSubtype(type, t, context)) {
                    haveToAdd = false;
                    break;
                }
            }

            if (haveToAdd) {
                v.add(type);
            }

            return haveToAdd;
        }
        
        return false;
    }

    /**
     * Adds all elements from c into this set.
     */
    public boolean addAll(Collection<? extends Type> c) {
	if (c == null) {
	    return false;
	}

	boolean changed = false;

	for (Type t : c) {
	    changed |= add(t); 
	}

	return changed;
    }

    /**
     * Removes all elements from the set
     */
    public void clear() {
	v.clear();
    }

    /**
     * Check whether object <code>o</code> is in the set. Because of the 
     * semantics of the subtype set, <code>o</code> is in the set iff
     * it descends from (or is equal to) one of the elements in the set.
     */
    public boolean contains(Object o) {
	if (o instanceof Type) {
	    Type type = (Type) o;
	    
	    Context context = ts.emptyContext();
	    
	    for (Iterator<Type> i = v.iterator(); i.hasNext(); ) {
		Type t = (Type) i.next();
		if (ts.isSubtype(type, t, context)) {
		    return true;
		}
	    }
	}

	return false;
    }

    /**
     * Check whether the type <code>t</code> or a subtype is in the set.
     * Returns true iff it descends from, is equal to, or is a super type of
     * one of the elements in the set.
     */
    public boolean containsSubtype(Type type) {
	Context context = ts.emptyContext();
	for (Iterator<Type> i = v.iterator(); i.hasNext(); ) {
	    Type t = (Type)i.next();
	    if (ts.isSubtype(type, t, context) || ts.isSubtype(t, type, context)) return true;
	}

	return false;
    }

    /**
     * Checks whether all elements of the collection are in the set
     */
    public boolean containsAll(Collection<?> c) {
	for (Object o : c) {
	    if (! contains(o)) {
		return false;
	    }
	}

	return true;
    }

    public boolean isEmpty() {
	return v.isEmpty();
    }
    
    public Iterator<Type> iterator() {
	return v.iterator();
    }

    /**
     * Removes all elements <code>s</code> in the set such that 
     * <code>s</code> decends from <code>o</code>
     *
     * @return whether or not an element was removed.
     */
    public boolean remove(Object o) {
        if (! (o instanceof Type)) {
            return false;
        }

        Type type = (Type) o;

	Context context = ts.emptyContext();

        boolean removed = false;

	for (Iterator<Type> i = v.iterator(); i.hasNext(); ) {
	    Type t = (Type) i.next();

	    if (ts.isSubtype(t, type, context)) {
		removed = true;
		i.remove(); 
	    }
	}

	return removed;
    }
    
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (Object o : c) {
	    changed |= remove(o);
	}

	return changed;
    }

    public boolean retainAll(Collection<?> c) {
	throw new UnsupportedOperationException("Not supported");
    }

    public int size() {
	return v.size();
    }

    public Object[] toArray() {
	return v.toArray();
    }

    public <S> S[] toArray(S[] a) {
	return v.toArray(a);
    }

    public String toString() {
	return v.toString();
    }
}
