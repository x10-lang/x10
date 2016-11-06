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
import x10.util.resilient.localstore.ResilientStore;
import x10.util.resilient.localstore.ResilientNativeMap;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.HashMap;

public class NativeStore[V]{V haszero, V <: Cloneable} extends Store[V] {
  static final class NativeLogEntry[V] implements Cloneable {
    val value:V;
    val placeId:Long;
    val key2:String;
    val value2:V;

    def this(value:V, placeId:Long, key2:String, value2:V) {
      this.value = value;
      this.placeId = placeId;
      this.key2 = key2;
      this.value2 = value2;
    }

    public def clone() = new NativeLogEntry[V](value, placeId, key2, value2);
  }

  val store:ResilientStore;
  
  val map:ResilientNativeMap;
  val log:ResilientNativeMap;

  def this(name:String, activePlaces:PlaceGroup) {
    store = ResilientStore.make(activePlaces);
    map = store.makeMap("_map_" + name);
    log = store.makeMap("_log_" + name);
  }

  public def get(key:String) = map.get(key) as V;

  public def set(key:String, value:V) {
    map.set(key, value);
  }
  
  public def setAll(pairs:HashMap[String,V]) {
	  val tmp = new HashMap[String,Cloneable]();
	  val iter = pairs.keySet().iterator();
	  while (iter.hasNext()) {
          val k = iter.next();
		  val v = pairs.getOrThrow(k) as Cloneable;
		  tmp.put(k,v);
	  }
	  map.setAll(tmp);
  } 

  public def set2(key:String, value:V, place:Place, key2:String, value2:V) {
    val placeId = store.getActivePlaces().indexOf(place);
    log.set(key, new NativeLogEntry(value, placeId, key2, value2));
    finish {
      at (place) async map.set(key2, value2);
      map.set(key, value);
    }
    log.delete(key);
  }

  public def getActivePlaces() = store.getActivePlaces();

  // update for changes in the active PlaceGroup
  public def updateForChangedPlaces(changes:ChangeDescription):void {
    store.updateForChangedPlaces(changes);
    val group = store.getActivePlaces();
    group.broadcastFlat(() => {
      for(key in log.keySet()) {
        Console.ERR.println("Replaying transaction log for key " + key);
        val entry = log.get(key) as NativeLogEntry[V];
        finish {
          at (group(entry.placeId)) async map.set(entry.key2, entry.value2);
          map.set(key, entry.value);
        }
        log.delete(key);
      }
    });
  }
}
