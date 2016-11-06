/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient.localstore;

import x10.util.HashSet;
import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;
import x10.xrx.Runtime;

/*Assumption: no local conflicts*/
public class MasterStore {
    private val moduleName = "MasterStore";
    private val lock = new Lock();    
    private val maps:HashMap[String,MapData];
    
    private val virtualPlaceId:Long;
    val sequence:AtomicLong = new AtomicLong();
    
    //used for original active places joined before any failured
    public def this(virtualPlaceId:Long) {
        this.virtualPlaceId = virtualPlaceId;
        this.maps = new HashMap[String,MapData]();
    }
    
    //used when a spare place is replacing a dead one
    public def this(virtualPlaceId:Long, masterMaps:HashMap[String,HashMap[String,Cloneable]]) {
        this.virtualPlaceId = virtualPlaceId;   
        this.maps = new HashMap[String,MapData]();
        
        if (masterMaps != null) {
        	val iter = masterMaps.keySet().iterator();
        	while (iter.hasNext()) {
        		val mapName = iter.next();
        		val mapData = masterMaps.getOrThrow(mapName);
        		this.maps.put(mapName, new MapData(mapData));
        	}
        }
    }    
   
    public def getState():MasterState {
        try {
            lock.lock();
            val tmp = new HashMap[String,HashMap[String,Cloneable]]();            
            val iter = maps.keySet().iterator();
            while (iter.hasNext()) {
            	val mapName = iter.next();
            	val mapData = maps.getOrThrow(mapName);
            	tmp.put(mapName, mapData.data);
            }            
            return new MasterState(tmp);
        }
        finally {
            lock.unlock();
        }
    }
    
    public def getMapData(mapName:String):MapData {
    	var data:MapData = null;
    	try {
             lock.lock();
             data = maps.getOrElse(mapName, null);
             if (data == null) {
            	 data = new MapData();
            	 maps.put(mapName, data);
             }
        }
        finally {
            lock.unlock();
        }
    	return data;
    }
}
