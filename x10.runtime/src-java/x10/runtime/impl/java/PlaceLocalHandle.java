/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.impl.java;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements java.io.Serializable {
//  private final Object[] objects;
    transient private final Object[] objects;
    
    // single process implementation
    private static final java.util.ArrayList<PlaceLocalHandle<?>> handles = new java.util.ArrayList<PlaceLocalHandle<?>>(); // all place local handles in this process
    private int id; // unique id of this place local handle
    private Object writeReplace() {
        if (this.id == 0) { 
            synchronized (handles) {
                if (this.id == 0) { // guard for multi thread
                    handles.add(this);
                    this.id = handles.size();
                }
            }
        }
        return this;
    }
    private Object readResolve() {
        synchronized (handles) {
            assert this.id != 0;
            PlaceLocalHandle<?> orig = handles.get(this.id - 1);
            assert orig != null && orig.id == this.id;
            return orig;
        }
    }

  public PlaceLocalHandle(Object t) {
    objects = new Object[Runtime.MAX_PLACES];
  }

  public T apply$G() {
    int here = Thread.currentThread().home().id;
    Object data = objects[here];
    assert data != null : "At "+here+": get called on uninitialized local object";
    return (T)data;
  }

  public void set_0_$$x10$lang$PlaceLocalHandle_T(T data) {
    int here = Thread.currentThread().home().id;
    assert objects[here] == null : "At "+here+" set called on already initialized local object";
    objects[here] = data;
  }
}
