/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.resilient.store;

import x10.util.resilient.localstore.Cloneable;

// a collection of resilient stores, one per place in a place group
public abstract class Store[V]{V haszero, V <: Cloneable} {
    // get the value for the given key in the local store
    public abstract def get(key:String):V;

    // set the value for the given key in the local store
    public abstract def set(key:String, value:V):void;

    // get the value for the given key at the specified place
    public def getRemote(place:Place, key:String) = at (place) get(key);

    // set the value for the given key at the specified place
    public def setRemote(place:Place, key:String, value:V) { at(place) set(key, value); }

    // set the value of a local and a remote key
    public abstract def set2(key:String, value:V, place:Place, key2:String, value2:V):void;

    // get current place group for the store
    public abstract def getActivePlaces():PlaceGroup;

    // update the place group after a place failure
    public abstract def recoverDeadPlaces():void;

    // instantiate a resilient store with the specified number of spare places
    public static def make[V](name:String, spares:Long){V haszero, V <: Cloneable} {
        val useHazelcast = "Hazelcast".equalsIgnoreCase(java.lang.System.getProperty("X10RT_DATASTORE", "none"));
        if (useHazelcast) {
            return new HazelcastStore[V](name, spares);
        } else {
            return new NativeStore[V](name, spares);
        }
    }
}
