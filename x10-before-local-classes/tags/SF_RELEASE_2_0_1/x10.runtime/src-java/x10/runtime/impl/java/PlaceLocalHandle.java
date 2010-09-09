/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
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
