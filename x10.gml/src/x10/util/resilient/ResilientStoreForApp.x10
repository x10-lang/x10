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
    class SnapshottableEntryKey(snapshottable:Snapshottable, keepOldSnapshot:Boolean) { }

    private val snapshots = new Rail[HashMap[SnapshottableEntryKey,DistObjectSnapshot]](2, (Long)=>new x10.util.HashMap[SnapshottableEntryKey,DistObjectSnapshot]());
    private var tempSnapshot:HashMap[SnapshottableEntryKey,DistObjectSnapshot] = null;
    private transient var commitCount:Long = 0L;
    
    public def startNewSnapshot() {
        Debug.assure(tempSnapshot==null, "Previous snapshot was not committed!");
        val idx = (commitCount+1) % 2;
        tempSnapshot = snapshots(idx);
    }

    /** Cancel the current snapshot, in case of failure during checkpoint. */
    public def cancelSnapshot() {
        tempSnapshot = null;
    }
    
    public def save(distObject:Snapshottable) {
        save(distObject, false);
    }
    
    public def save(distObject:Snapshottable, snapshot:DistObjectSnapshot) {
        save(distObject, snapshot, false);
    }
    
    public def save(distObject:Snapshottable, keepOldSnapshot:Boolean) {
        Debug.assure(tempSnapshot != null, "New snapshot should be started first");
        val snapshot = distObject.makeSnapshot();
        save(distObject, snapshot, keepOldSnapshot);
    }
    
    public def save(distObject:Snapshottable, snapshot:DistObjectSnapshot, keepOldSnapshot:Boolean) {
        Debug.assure(tempSnapshot != null, "New snapshot should be started first");
        val snapshotKey = new SnapshottableEntryKey(distObject,keepOldSnapshot);
        atomic tempSnapshot.put(snapshotKey, snapshot);
    }
    
    public def commit() {
        val idx = commitCount % 2;
        commitCount++; // switch to the new snapshot
        tempSnapshot = null;

        // delete the old snapshot
        val oldSnapshot = snapshots(idx);
        val iter = oldSnapshot.keySet().iterator(); 
        while (iter.hasNext()) {
            val key = iter.next();
            val objectStore = oldSnapshot.getOrElse(key, null);
            if (objectStore != null && !key.keepOldSnapshot) {
                try { objectStore.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
            }
        }
        oldSnapshot.clear();
    }

    public def restore() {
        val idx = commitCount % 2;
        val appSnapshot = snapshots(idx);
        val iter = appSnapshot.keySet().iterator();
        finish{
            while (iter.hasNext()) {
                val key = iter.next();
                val distObjectSnapshot = appSnapshot.getOrElse(key, null);
                async key.snapshottable.restoreSnapshot(distObjectSnapshot);
            }
        }
    }
}

