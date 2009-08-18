/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * Runtime service routines to create, manage, and destory place-local storage.
 */
public final class PlaceLocalStorage {

  public static def createDistributedObject[T](dist:Dist, init:(Place)=>T):PlaceLocalHandle[T] {
    val handle = at(Place.FIRST_PLACE) PlaceLocalHandle.createHandle[T]();
    finish for (p in dist.places()) {
       async (p) handle.set(init(here));
    }
    return handle;
  }

}