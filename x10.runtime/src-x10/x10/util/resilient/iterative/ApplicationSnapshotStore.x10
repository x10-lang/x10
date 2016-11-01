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

package x10.util.resilient.iterative;

import x10.util.HashMap;
import x10.util.resilient.localstore.*;

public class ApplicationSnapshotStore {
    val map:HashMap[String,SnapshotRecord] = new HashMap[String,SnapshotRecord]();
    var firstCheckpoint:Boolean;
    var lastVersion:Long = -1;
    
    public def save(key:String, distObject:Snapshottable) {
    	map.put(key, new SnapshotRecord(distObject,false));
    }
    
    public def saveReadOnly(key:String, distObject:Snapshottable) {
    	map.put(key, new SnapshotRecord(distObject,true));
    }
    
    public def getCheckpointData_local(first:Boolean):HashMap[String,Cloneable] {
    	val ckptMap = new HashMap[String,Cloneable]();
    	val iter = map.keySet().iterator();
    	while (iter.hasNext()) {
    		val key =  iter.next();
    		val record = map.getOrThrow(key);
    		val cloneable = record.snapshottable.makeSnapshot_local();
    		if (!record.readOnly || (record.readOnly && first)) {
    			ckptMap.put(key,cloneable);	
    		}
    	}
    	return ckptMap;
    }
       
    public def restore_local(restoreDataMap:HashMap[String,Cloneable]) {
    	val iter = map.keySet().iterator();
    	while (iter.hasNext()) {
    		val key =  iter.next();
    		val record = map.getOrThrow(key);
    		val cloneable = restoreDataMap.getOrThrow(key);
    		record.snapshottable.restoreSnapshot_local(cloneable);
    	}
    }
    
    def nextCheckpointVersion() {
    	return (lastVersion+1)%2;
    }
    
    def commitCheckpoint(newVersion:Long) {
    	lastVersion = newVersion;
    }
    
    def getRestoreKeyVersions():HashMap[String,Long] {
    	val versionsMap = new HashMap[String,Long]();
    	val iter = map.keySet().iterator();
    	while (iter.hasNext()) {
    		val key =  iter.next();
    		val record = map.getOrThrow(key);    		
    		if (record.readOnly) {
    			versionsMap.put(key,0);	
    		}
    		else {
    			versionsMap.put(key,lastVersion);
    		}
    	}
    	return versionsMap;
    }
}

class SnapshotRecord(snapshottable:Snapshottable, readOnly:Boolean) {
	var version:Long = -1;
}