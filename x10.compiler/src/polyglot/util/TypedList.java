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

/**
 * A TypedList is an List which will not allow members not belonging
 * to a given type to be added to a collection.  Optionally, it may
 * also present an immutable view.
 *
 * If an attempt is made to change an immutable list, or if an attempt
 * is made to insert an improperly-typed element, an
 * UnsupportedOperationException is thrown.
 *
 * This class is given so that we can present a List for a given class
 * without worrying about outsiders breaking the rep.
 *
 * This is a poor substitute for PolyJ.
 **/
public class TypedList<T> implements List<T>, java.io.Serializable, Cloneable
{
  static final long serialVersionUID = -1390984392613203018L;

  /**
   * Requires: <list> not null, and every element of <list> may be
   *    cast to class <c>.
   * Creates a new TypedList, containing all the elements of <list>,
   * which restricts all members to belong to class <c>.  If <c> is
   * null, no typing restriction is made.  If <immutable> is true, no
   * modifications are allowed.
   **/
  public static <T> TypedList<T> copy(List<? extends T> list, Class<? super T> c, boolean immutable) {
    if (list == null)
      return null;
    return new TypedList<T>(new ArrayList<T>(list), c, immutable);
  }

  /**
   * Creates a new TypedList, containing all the elements of <list>,
   * which restricts all members to belong to class <c>.  If <c> is
   * null, no typing restriction is made.  If <immutable> is true, no
   * modifications are allowed.
   *
   * Throws an UnsupportedOperationException if any member of <list>
   * may not be cast to class <c>.
   **/
  public static <T> TypedList<T> copyAndCheck(List<? extends T> list, Class<? super T> c, boolean immutable) {
    if (c != null)
      check(list,c);
    return copy(list,c,immutable);
  }

  /**
   * Throws an UnsupportedOperationException if any member of <list>
   * may not be cast to class <c>. Otherwise does nothing.
   **/
  public static <T> void check(List<? extends T> list, Class<? super T> c) {
    if (list == null)
      return;
    for (T o : list) {
      if (o != null && !c.isAssignableFrom(o.getClass())) {
          throw new UnsupportedOperationException(
		     "Tried to add a " + o.getClass().getName() +
   	             " to a list of type " + c.getName());
      }
    }
  }

  /**
   * Requires: <list> not null, and every element of <list> may be
   *    cast to class <c>.
   * Effects:
   * Creates a new TypedList around <list> which restricts all
   * members to belong to class <c>.  If <c> is null, no typing
   * restriction is made.  If <immutable> is true, no modifications
   * are allowed.
   **/
  public TypedList(List<T> list, Class<? super T> c, boolean immutable) {
    this.immutable = immutable;
    this.allowed_type = c;
    this.backing_list = list;
  }

  /**
   * Gets the allowed type for this list.
   **/
  public Class<? super T> getAllowedType(){
    return allowed_type;
  }

  /**
   * Copies this list.
   **/
  @SuppressWarnings("unchecked") // Casting to a generic type
  public TypedList<T> copy() {
      return (TypedList<T>) clone();
  }

  @SuppressWarnings("unchecked") // Casting to a generic type
  public Object clone() {
      try {
          TypedList<T> l = (TypedList<T>) super.clone();
          l.backing_list = new ArrayList<T>(backing_list);
          return l;
      }
      catch (CloneNotSupportedException e) {
          throw new InternalCompilerError("Java clone weirdness.");
      }
  }

  public void add(int idx, T o) {
    tryIns(o);
    backing_list.add(idx,o);
  }
  public boolean add(T o) {
    tryIns(o);
    return backing_list.add(o);
  }
  public boolean addAll(int idx, Collection<? extends T> coll) {
    tryIns(coll);
    return backing_list.addAll(idx, coll);
  }
  public boolean addAll(Collection<? extends T> coll) {
    tryIns(coll);
    return backing_list.addAll(coll);
  }
  public ListIterator<T> listIterator() {
    return new TypedListIterator<T>(backing_list.listIterator(),
				 allowed_type,
				 immutable);
  }
  public ListIterator<T> listIterator(int idx) {
    return new TypedListIterator<T>(backing_list.listIterator(idx),
				 allowed_type,
				 immutable);
  }
  public T set(int idx, T o) {
    tryIns(o);
    return backing_list.set(idx, o);
  }
  public List<T> subList(int from, int to) {
    return new TypedList<T>(backing_list.subList(from, to),
			 allowed_type,
			 immutable);
  }

    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (backing_list == o)
	    return true;
	if (! (o instanceof List<?>))
	    return false;
	if (((List<?>) o).size() != backing_list.size())
	    return false;
	if (o instanceof TypedList<?>)
	    return backing_list.equals(((TypedList<?>) o).backing_list);
	else
	    return backing_list.equals(o);
    }

  public void clear() 
    { tryMod(); backing_list.clear(); }
  public boolean contains(Object o) 
    { return backing_list.contains(o); }
  public boolean containsAll(Collection<?> coll) 
    { return backing_list.containsAll(coll); }
  public T get(int idx)
    { return backing_list.get(idx); }
  public int hashCode()
    { return backing_list.hashCode(); }
  public int indexOf(Object o)
    { return backing_list.indexOf(o); }
  public boolean isEmpty()
    { return backing_list.isEmpty(); }
  public Iterator<T> iterator()
    { return listIterator(); }
  public int lastIndexOf(Object o)
    { return backing_list.lastIndexOf(o); }
  public T remove(int idx)
    { tryMod(); return backing_list.remove(idx); }
  public boolean remove(Object o)
    { tryMod(); return backing_list.remove(o); }
  public boolean removeAll(Collection<?> coll)
    { tryMod(); return backing_list.removeAll(coll); }
  public boolean retainAll(Collection<?> coll)
    { tryMod(); return backing_list.retainAll(coll); }
  public int size()
    { return backing_list.size(); }
  public Object[] toArray() 
    { return backing_list.toArray(); }
  public <S> S[] toArray(S[] oa)
    { return backing_list.toArray(oa); }
  public String toString()
    { return backing_list.toString(); }

  private final void tryIns(T o) {
    if (immutable) 
      throw new UnsupportedOperationException(
			         "Add to an immutable TypedListIterator");
    
    if (allowed_type != null && o != null &&
	!allowed_type.isAssignableFrom(o.getClass())) {
      String why = "Tried to add a " + o.getClass().getName() +
	" to a list of type " + allowed_type.getName();
      throw new UnsupportedOperationException(why);
    }    
  }

  private final void tryIns(Collection<? extends T> coll) {
    if (immutable) 
      throw new UnsupportedOperationException(
			         "Add to an immutable TypedListIterator");
    
    for (Iterator<? extends T> it = coll.iterator(); it.hasNext(); ) {
      T o = it.next();
      tryIns(o);
    }
  }

  private final void tryMod() {
    if (immutable) 
      throw new UnsupportedOperationException(
			         "Change to an immutable TypedListIterator");
  }

  // RI: allowed_type may be null.
  private Class<? super T> allowed_type;
  private boolean immutable;
  private List<T> backing_list;
}

