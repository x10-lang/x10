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

import java.util.ListIterator;
import java.lang.UnsupportedOperationException;

/**
 * A TypedListIterator is an ListIterator which will not allow members
 * not belonging to a given type to be added to a collection.
 * Optionally, it may also present an immutable view.
 *
 * If an attempt is made to change an immutable listiterator, or if
 * an attempt is made to insert an improperly-typed element, an
 * UnsupportedOperationException is thrown.
 *
 * This class is given so that we can present a ListIterator for a
 * given given class without worrying about outsiders breaking the
 * rep.
 *
 * This is a poor substitute for PolyJ.
 **/
public class TypedListIterator<T> implements ListIterator<T> {
  /**
   * Requires: <iter> not null
   * Creates a new TypedIterator around <iter> which restricts all
   * members to belong to class <c>.  If <c> is null, no typing
   * restriction is made.  If <immutable> is true, no modifications
   * are allowed.
   **/
  public TypedListIterator(ListIterator<T> iter, Class<? super T> c, boolean immutable) {
    this.immutable = immutable;
    this.allowed_type = c;
    this.backing_iterator = iter;
  }

  /**
   * Gets the allowed type for this ListIterator.
   **/
  public Class<? super T> getAllowedType(){
    return allowed_type;
  }

  public void add(T o) {
    tryIns(o);
    backing_iterator.add(o);
  }

  public void set(T o) {
    tryIns(o);
    backing_iterator.set(o);   
  }

  public boolean hasNext()     { return backing_iterator.hasNext(); }
  public boolean hasPrevious() { return backing_iterator.hasPrevious(); }
  public T next()         { return backing_iterator.next(); }
  public int nextIndex()       { return backing_iterator.nextIndex(); }
  public T previous()     { return backing_iterator.previous(); }
  public int previousIndex()   { return backing_iterator.previousIndex(); }
  public void remove()         { 
    if (immutable) 
      throw new UnsupportedOperationException(
			         "Remove from an immutable TypedListIterator");
    
    backing_iterator.remove();
  }

  private final void tryIns(T o) {
    if (immutable) 
      throw new UnsupportedOperationException(
			         "Add to an immutable TypedListIterator");
    
    if (allowed_type != null && 
	!allowed_type.isAssignableFrom(o.getClass())) {
      String why = "Tried to add a " + o.getClass().getName() +
	" to a list of type " + allowed_type.getName();
      throw new UnsupportedOperationException(why);
    }    
  }

  // RI: allowed_type may be null.
  private Class<? super T> allowed_type;
  private boolean immutable;
  private ListIterator<T> backing_iterator;
}

