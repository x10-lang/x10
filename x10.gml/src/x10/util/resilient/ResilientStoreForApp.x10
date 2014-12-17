/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package x10.util.resilient;

import x10.util.HashMap;
import x10.matrix.util.Debug;

public class ResilientStoreForApp {

    private val snapshots = new Rail[ApplicationSnapshot](2, (Long)=>new ApplicationSnapshot());
    private var tempSnapshot:ApplicationSnapshot = null;
    private transient var commitCount:Long = 0L;

    private def searchSnapshot(distObject:Snapshottable):DistObjectSnapshot {        
        val consistentSnapshot = getConsistentSnapshot();
        val appSnapshotMap = consistentSnapshot.map;
        val iter = appSnapshotMap.keySet().iterator();
        var snapshot:DistObjectSnapshot = null;
        while (iter.hasNext()) {
            val key = iter.next();
            if (key.snapshottable == distObject){
                snapshot = appSnapshotMap.getOrElse(key, null);
                break;
            }
        }        
        return snapshot;
    }
    
    private def getConsistentSnapshot():ApplicationSnapshot{
        val idx = commitCount % 2;
        return snapshots(idx);
    }  
    
    public def startNewSnapshot() {
        Debug.assure(tempSnapshot==null, "Previous snapshot was not committed!");
        val idx = (commitCount+1) % 2;
        tempSnapshot = snapshots(idx);
    }

    /** Cancel the current snapshot, in case of failure during checkpoint. */
    public def cancelSnapshot() {
        if (tempSnapshot != null)
            tempSnapshot.delete();
        tempSnapshot = null;
    }
    
    public def save(distObject:Snapshottable) {
        save(distObject, null, false);
    }
    
    public def saveReadOnly(distObject:Snapshottable) {
        val snapshot = searchSnapshot(distObject);        
        save(distObject, snapshot, true);
    }
    
    private def save(distObject:Snapshottable, snapshot:DistObjectSnapshot, keepOldSnapshot:Boolean) {
        Debug.assure(tempSnapshot != null, "New snapshot should be started first");        
        val snapshotKey = new SnapshottableEntryKey(distObject, keepOldSnapshot);
        atomic tempSnapshot.map.put(snapshotKey, snapshot);
    }

    private def saveAll(){
        val appSnapshotMap = tempSnapshot.map; 
        val iter = appSnapshotMap.keySet().iterator();    
        finish{
            while (iter.hasNext()) {
                val key = iter.next();
                var distObjectSnapshot:DistObjectSnapshot = appSnapshotMap.getOrElse(key, null);                
                if (distObjectSnapshot == null) {
                    async{
                        val snapshot = key.snapshottable.makeSnapshot();
                        atomic appSnapshotMap.put(key, snapshot);
                    }
                }                
            }
        }        
    }
    
    public def commit() {
        saveAll();
        
        val idx = commitCount % 2;
        commitCount++; // switch to the new snapshot
        tempSnapshot = null;

        // delete the old snapshot
        val oldSnapshot = snapshots(idx);
        oldSnapshot.delete();
    }

    public def restore() {
        val appSnapshot = getConsistentSnapshot();
        val iter = appSnapshot.map.keySet().iterator();
        finish{
            while (iter.hasNext()) {
                val key = iter.next();
                val distObjectSnapshot = appSnapshot.map.getOrElse(key, null);
                async key.snapshottable.restoreSnapshot(distObjectSnapshot);
            }
        }
    }
}

class SnapshottableEntryKey(snapshottable:Snapshottable, keepOldSnapshot:Boolean) { }

class ApplicationSnapshot {
    var map:HashMap[SnapshottableEntryKey,DistObjectSnapshot] = new x10.util.HashMap[SnapshottableEntryKey,DistObjectSnapshot]();

    def delete():void{
        val iter = map.keySet().iterator(); 
        while (iter.hasNext()) {
            val key = iter.next();
            val objectStore = map.getOrElse(key, null);
            if (objectStore != null && !key.keepOldSnapshot) {
                try { objectStore.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
            }
        }
        map.clear();
    }
}
