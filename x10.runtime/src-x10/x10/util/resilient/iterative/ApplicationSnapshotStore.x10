/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Sara Salem Hamouda 2014-2015.
 */
package x10.util.resilient.iterative;

import x10.util.HashMap;
import x10.regionarray.Dist;

public class ApplicationSnapshotStore {
   
    private transient val snapshots:Rail[ApplicationSnapshot] = new Rail[ApplicationSnapshot](2, (Long)=>new ApplicationSnapshot());
    private transient var tempSnapshot:ApplicationSnapshot = null;
    private transient var commitCount:Long = 0L;
    private transient var places:PlaceGroup;
    
    private val isLocalView:Boolean;
    public def this(){
        this.isLocalView = false;
        this.places = null;
    }
    
    public def this(local:Boolean, plc:PlaceGroup){
        this.isLocalView = local;
        this.places = plc;
    }

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
        if (tempSnapshot!=null)
            Console.OUT.println("Previous snapshot was not committed!");        
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
        if (tempSnapshot==null)
            Console.OUT.println("New snapshot should be started first");        
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
    
    private def saveAll_local(){
        val appSnapshotMap = tempSnapshot.map;        
        //prepare the snapshot objects at the root place        
        val iter = appSnapshotMap.keySet().iterator();        
        while (iter.hasNext()) {            
            val key = iter.next();            
            var distObjectSnapshot:DistObjectSnapshot = appSnapshotMap.getOrElse(key, null);               
            if (distObjectSnapshot == null) {
                val snapshot = DistObjectSnapshot.make();
                appSnapshotMap.put(key, snapshot);
                key.ignore = false;
            }
            else
                key.ignore = true;
        }
        
        finish ateach(Dist.makeUnique(places)) {
            val iter2 = appSnapshotMap.keySet().iterator();
            while (iter2.hasNext()) {
                val key = iter2.next();
                if (!key.ignore) {
                    var distObjectSnapshot:DistObjectSnapshot = appSnapshotMap.getOrElse(key, null);
                    val snapshot = appSnapshotMap.getOrElse(key, null);                    
                    async key.snapshottable.makeSnapshot_local(snapshot);
                }
            }
        }
    }
    
    public def commit() {
        if (isLocalView)
            saveAll_local();
        else
            saveAll();
        
        val idx = commitCount % 2;
        commitCount++; // switch to the new snapshot
        tempSnapshot = null;

        // delete the old snapshot
        val oldSnapshot = snapshots(idx);
        oldSnapshot.delete();
    }
    
    public def restore() {
        if (isLocalView)
            restore_local();
        else{
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
    
    private def restore_local() {
        val appSnapshot = getConsistentSnapshot();
        val appSnapshotMap = appSnapshot.map;
       
        finish ateach(Dist.makeUnique(places)) {
            val iter = appSnapshotMap.keySet().iterator();
            while (iter.hasNext()) {
                val key = iter.next();
                val distObjectSnapshot = appSnapshotMap.getOrElse(key, null);
                async key.snapshottable.restoreSnapshot_local(distObjectSnapshot);                
            }
        }
    }
    
    
    public def updatePlaces(newPlaces:PlaceGroup){
        this.places = newPlaces;
    }
}

class SnapshottableEntryKey(snapshottable:Snapshottable, keepOldSnapshot:Boolean) {
    var ignore:Boolean = false;
}

class ApplicationSnapshot {
    var map:HashMap[SnapshottableEntryKey,DistObjectSnapshot] = new x10.util.HashMap[SnapshottableEntryKey,DistObjectSnapshot]();
    
    def delete():void{
        val iter = map.keySet().iterator(); 
        while (iter.hasNext()) {
            val key = iter.next();
            val objectStore = map.getOrElse(key, null);
            //FIXME: cancel snapshot should delete the object anyway
            if (objectStore != null && !key.keepOldSnapshot) {
                try { objectStore.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
            }
        }
        map.clear();
    }
}
