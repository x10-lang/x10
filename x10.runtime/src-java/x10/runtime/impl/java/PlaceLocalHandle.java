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

import java.util.HashMap;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements java.io.Serializable {
    private static final HashMap<Integer,Object> data = new HashMap<Integer,Object>();
    
	private static final int placeShift = 20;
	private static int nextLocalId = 1;

	transient private boolean initialized = false;
	transient private Object myData = null;
	private final int id;
    
	// TODO: The X10 code currently ensures that PlaceLocalHandle's are only
	//       created at Place 0 by doing an at.  We've contemplated moving to
	//       more of a SVD style implementation where each place would be able to
	//       create place local handles by either encoding the place in the id like we
	//       did here or by having the places get ids in "chunks" from the master id server
	//       at place 0. 
	//       Since we are thinking about making this change, I went ahead and did a poor-man's
	//       version of it here instead of asserting nextId is only called at place 0 
	//       (which would have been true currently).
	private static synchronized int nextId() {
	    int here = Thread.currentThread().home().id;
	    int newId  = nextLocalId++;
	    assert newId < (1<< placeShift);
	    newId |= (here << placeShift);
	    return newId;
	}
	
    private Object readResolve() {
    	initialized = false;
    	return this;
    }

  public PlaceLocalHandle(x10.rtt.Type<T> T) {
	  id = nextId();
  }

  // TODO haszero
  // zero value constructor
  public PlaceLocalHandle(x10.rtt.Type<T> T, java.lang.System[] dummy$0) {
      this(T);
  }

  public T $apply$G() {
	  if (!initialized) {
		  synchronized(data) {
			  myData = data.get(id);
			  initialized = true;
		  }
	  }
	  return (T) myData;
  }

  public void set_0_$$x10$lang$PlaceLocalHandle_T(T value) {
	  synchronized(data) {
		  Object old = data.put(id, value);
		  assert old == null : "Set called on already initialized local object";
	  }
  }

  @Override
  public String toString() {
      return "PlaceLocalHandle(" + this.id + ")";
  }

}
