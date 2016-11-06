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

import x10.util.resilient.ResilientMap;
import x10.util.resilient.localstore.Cloneable;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.HashMap;

public class HazelcastStore[V]{V haszero, V <: Cloneable} extends Store[V] {
  static final class LogEntry[V] {
    val key:String;
    val value:V;
    val key2:String;
    val value2:V;

    def this(key:String, value:V, key2:String, value2:V) {
      this.key = key;
      this.value = value;
      this.key2 = key2;
      this.value2 = value2;
    }
  }

  val map:ResilientMap[String,V];
  val log:ResilientMap[String,LogEntry[V]];
  var group:PlaceGroup;

  def this(name:String, activePlaces:PlaceGroup) {
    map = ResilientMap.getMap[String,V]("_map_" + name);
    log = ResilientMap.getMap[String,LogEntry[V]]("_log_" + name);
    this.group = activePlaces;
  }

  private def k(place:Place, key:String) = (group.indexOf(place) << 32) + "_" + key;

  public def get(key:String) = getRemote(here, key);

  public def set(key:String, value:V) {
    setRemote(here, key, value);
  }

  public def setAll(pairs:HashMap[String,V]) {
	  val iter = pairs.keySet().iterator();
	  while (iter.hasNext()) {
		  val k = iter.next();
		  val v = pairs.getOrThrow(k);
		  set (k,v);
	  }
  }
  
  public def getRemote(place:Place, key:String) = map.get(k(place, key));

  public def setRemote(place:Place, key:String, value:V) {
    map.set(k(place, key), value);
  }

  public def set2(key:String, value:V, place:Place, key2:String, value2:V) {
    val k1 = k(here, key);
    val k2 = k(place, key2);
    log.set(k1, new LogEntry(k1, value, k2, value2));
    map.set(k2, value2);
    map.set(k1, value);
    log.delete(k1);
  }

  public def getActivePlaces() = group;

  public def updateForChangedPlaces(changes:ChangeDescription):void {
    group = changes.newActivePlaces;
    for(entry in log.values()) {
      Console.ERR.println("Replaying transaction log for key " + entry.key);
      map.set(entry.key2, entry.value2);
      map.set(entry.key, entry.value);
    }
    log.clear();
  }
}
