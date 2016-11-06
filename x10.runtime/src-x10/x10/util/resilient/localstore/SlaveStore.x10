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
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;
import x10.util.concurrent.Lock;

public class SlaveStore {
    private val moduleName = "SlaveStore";
    
    private val mastersMap:HashMap[Long,MasterState]; // master_virtual_id, master_data
    private transient val lock:Lock = new Lock();
    
    public def this() {
        mastersMap = new HashMap[Long,MasterState]();
    }
    
    public def addMasterPlace(masterVirtualId:Long, masterState:MasterState) {
        try {
            lock.lock();
            mastersMap.put(masterVirtualId, masterState);
        }
        finally {
            lock.unlock();
        }
    }
    
    public def getMasterState(masterVirtualId:Long):MasterState {
        try {
            lock.lock();
            var state:MasterState = mastersMap.getOrElse(masterVirtualId, null);
            if (state == null)
                return new MasterState(new HashMap[String,HashMap[String,Cloneable]]());
            return state;
        }
        finally {
            lock.unlock();
        }
    }
    
    public def commit(mapName:String, masterVirtualId:Long, transLog:HashMap[String,TransKeyLog]) {
    try {
            lock.lock();
            var state:MasterState = mastersMap.getOrElse(masterVirtualId, null);
            if (state == null) {
                state = new MasterState(new HashMap[String,HashMap[String,Cloneable]]());
            	mastersMap.put(masterVirtualId, state); 
            }
            applyChangesLockAcquired(mapName, state, transLog);
        }
        finally {
            lock.unlock();
        }
    }
    
    private def applyChangesLockAcquired(mapName:String, state:MasterState, transLog:HashMap[String,TransKeyLog]) {
        val data = state.getMapData(mapName);
        val iter = transLog.keySet().iterator();
        while (iter.hasNext()) {
            val key = iter.next();
            val log = transLog.getOrThrow(key);
            if (log.readOnly())
                continue;
            if (log.isDeleted()) 
                data.remove(key);
            else
                data.put(key, log.getValue());
        }
    }
}

class MasterState {
    public var maps:HashMap[String,HashMap[String,Cloneable]];
   
    public def this(maps:HashMap[String,HashMap[String,Cloneable]]) {
        this.maps = maps;
    }
    
    public def getMapData(mapName:String) {
    	var data:HashMap[String,Cloneable] = maps.getOrElse(mapName, null);
        if (data == null) {
        	data = new HashMap[String,Cloneable]();
        	maps.put(mapName, data);
        }
        return data;
    }
}