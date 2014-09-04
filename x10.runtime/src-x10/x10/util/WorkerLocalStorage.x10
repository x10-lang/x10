/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;


/**
 * A place-local worker-local storage facility.
 * 
 * For instance, the following program will non-deterministically output
 * Hello or Bye depending on which worker runs each async.
 * 
 * class Foo {
 *   static store = new x10.util.WorkerLocalStorage[String,String]();
 * 
 *   public static def main(Rail[String]) {
 *     finish at (here.next()) async {
 *       store.put("salutation", "Hello");
 *     }
 *     finish at (here.next()) async {
 *       Console.OUT.println(store.getOrElse("salutation", "Bye"));
 *     }
 *   }
 * }
 * 
 */
public class WorkerLocalStorage[Key,Value] {Value haszero} {
    private val store = PlaceLocalHandle.make(Place.places(),
            ():Rail[HashMap[Key,Value]] => new Rail[HashMap[Key,Value]](Runtime.MAX_THREADS));

    public def get(key:Key):Value {
        val id = Runtime.workerId();
        val localStore = store();
        if (null == localStore(id)) return Zero.get[Value]();
        return localStore(id).get(key);
    }

    public def getOrElse(key:Key, value:Value):Value {
        val id = Runtime.workerId();
        val localStore = store();
        if (null == localStore(id)) return value;
        return localStore(id).getOrElse(key, value);
    }

    public def getOrThrow(key:Key):Value {
        val id = Runtime.workerId();
        val localStore = store();
        if (null == localStore(id)) throw new NoSuchElementException("Not found");
        return localStore(id).getOrThrow(key);
    }

    public def put(key:Key, value:Value):Value {
        val id = Runtime.workerId();
        val localStore = store();
        if (null == localStore(id)) localStore(id) = new HashMap[Key,Value]();
        return localStore(id).put(key, value);
    }

    public def remove(key:Key):Value {
        val id = Runtime.workerId();
        val localStore = store();
        if (null == localStore(id)) return Zero.get[Value]();
        return localStore(id).remove(key);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
