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

  /**
   * Create a distributed object with local state of type T 
   * at each place in the argument distribution.  The local object will be initialized 
   * by evaluating init at each place.  When this method returns, the local objects
   * will be initialized and available via the returned PlaceLocalHandle instance
   * at every place in the distribution.
   * 
   * @param dist A distribution specifiying the places where local objects should be created.
   * @param init the initialization closure used to create the local object.
   * @return a PlaceLocalHandle that can be used to access the local objects.
   */
  public static def createDistributedObject[T](dist:Dist, init:(Place)=>T):PlaceLocalHandle[T] {
    val handle = at(Place.FIRST_PLACE) PlaceLocalHandle.createHandle[T]();
    finish for (p in dist.places()) {
       async (p) handle.set(init(here));
    }
    return handle;
  }
}