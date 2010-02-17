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
import x10.runtime.impl.java.Thread;
import x10.runtime.impl.java.Runtime;

/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T>{
  private final Object[] objects;

  public PlaceLocalHandle(Object t) {
    objects = new Object[Runtime.MAX_PLACES];
  }

  public T apply() {
    int here = Thread.currentThread().home();
    Object data = objects[here];
    assert data != null : "At "+here+": get called on uninitialized local object";
    return (T)data;
  }

  public void set(T data) {
    int here = Thread.currentThread().home();
    assert objects[here] == null : "At "+here+" set called on already initialized local object";
    objects[here] = data;
  }
}
