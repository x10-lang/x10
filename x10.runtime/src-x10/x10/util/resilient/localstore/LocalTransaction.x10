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

import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.HashSet;
import x10.compiler.Ifdef;
import x10.xrx.Runtime;

public class LocalTransaction (plh:PlaceLocalHandle[LocalStore], id:Long, mapName:String, masterMapData:MapData) {
private val moduleName = "LocalTransaction";

    private val transLog:HashMap[String,TransKeyLog] = new HashMap[String,TransKeyLog]();    
    private var alive:Boolean = true; // the transaction is alive if it can process more puts and gets

    public def put(key:String, newValue:Cloneable):Cloneable {
        assert(alive);
        val copiedValue = newValue.clone();    
        var oldValue:Cloneable = null;    
        val keyLog = transLog.getOrElse(key,null);
        if (keyLog != null) { // key used in the transaction before
            oldValue = keyLog.getValue();
            keyLog.update(copiedValue);
        }
        else {// first time to access this key
            val value = masterMapData.getNoCopy(key); // no need to copy, we will overwrite this value any way
            val log = new TransKeyLog(value);
            transLog.put(key, log);
            log.update(copiedValue);
            oldValue = value;
        }
        return oldValue;
    }
    
    
    public def delete(key:String):Cloneable {
    	assert(alive);
        var oldValue:Cloneable = null;    
        val keyLog = transLog.getOrElse(key,null);
        if (keyLog != null) { // key used in the transaction before
            oldValue = keyLog.getValue();
            keyLog.delete();
        }
        else {// first time to access this key
            val value = masterMapData.getNoCopy(key);// no need to copy, we will delete this value any way
            val log = new TransKeyLog(value);
            transLog.put(key, log);
            log.delete();
            oldValue = value;
        }
        return oldValue;
    }
    
    
    public def get(key:String):Cloneable {
    	assert(alive);
        var oldValue:Cloneable = null;
        val keyLog = transLog.getOrElse(key,null);
        if (keyLog != null) { // key used before in the transaction
           oldValue = keyLog.getValue();
        }
        else {
            val value = masterMapData.getCopy(key);
            val log = new TransKeyLog(value);
            transLog.put(key, log);
            oldValue = value;
        }
        return oldValue;
    }

    /**
     * Dead slave is fatal - 
     * client should take proper actions to ensure consistency (i.e. repeat the set operation after recovery)
     **/
    public def commit() {
    	try {
    		assert(alive);
    		if (isReadOnlyTransaction()){
    			alive = false;
    			return;
    		}
        
    		val masterVirtualId = plh().virtualPlaceId;
    		val tmpMapName = mapName;
    		val tmpTransLog = transLog;
    		val tmpPLH = plh;
		finish at (tmpPLH().slave) async {
    			tmpPLH().slaveStore.commit(tmpMapName, masterVirtualId, tmpTransLog);
    		}
    		//master commit
    		masterMapData.commit(id, transLog);
    		
    		alive = false;
    	} finally {    		
    		masterMapData.lock.unlock();
    	}
    }
    
    private def isReadOnlyTransaction():Boolean {
        var result:Boolean = true;
        val iter = transLog.keySet().iterator();
        while (iter.hasNext()) {
            val key = iter.next();
            val log = transLog.getOrThrow(key);
            if (!log.readOnly()) {
                result = false;
                break;
            }
        }
        return result;
    }
}