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

import x10.compiler.Native;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.resilient.localstore.Cloneable;
import x10.util.HashMap;

// a collection of resilient stores, one per place in a place group
public abstract class Store[V]{V haszero, V <: Cloneable} {
    // get the value for the given key in the local store
    public abstract def get(key:String):V;

    // set the value for the given key in the local store
    public abstract def set(key:String, value:V):void;

    // set multiple k/v pairs in the local store
    public abstract def setAll(pairs:HashMap[String,V]):void;

    // get the value for the given key at the specified place
    public def getRemote(place:Place, key:String) = at (place) get(key);

    // set the value for the given key at the specified place
    public def setRemote(place:Place, key:String, value:V) { at(place) set(key, value); }

    // set the value of a local and a remote key
    public abstract def set2(key:String, value:V, place:Place, key2:String, value2:V):void;

    // update for changes in the active PlaceGroup
    public abstract def updateForChangedPlaces(changes:ChangeDescription):void;

    // get the current PlaceGroup of active places where the store is available for use
    public abstract def getActivePlaces():PlaceGroup;
    
    // instantiate a resilient store over the specified PlaceGroup
    public static def make[V](name:String, activePlaces:PlaceGroup){V haszero, V <: Cloneable} {
        if ("Hazelcast".equals(dataStore())) {
            return new HazelcastStore[V](name, activePlaces);
        } else {
            return new NativeStore[V](name, activePlaces);
        }
    }

    @Native("java", "(x10.x10rt.X10RT.useHazelcastStore() ? \"Hazelcast\" : \"native\")")
    static def dataStore():String = "native";
}
