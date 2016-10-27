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
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.resilient.PlaceManager.ChangeDescription;

/**
 * A store that maintains a master + 1 backup (slave) copy
 * of the data.
 * The mapping between masters and slaves is specififed by
 * the next/prev operations on the activePlaces PlaceGroup.
 */
public class ResilientStore {
    private val moduleName = "ResilientStore";
    private val plh:PlaceLocalHandle[LocalStore];
    private var activePlaces:PlaceGroup;
    
    private def this(pg:PlaceGroup, plh:PlaceLocalHandle[LocalStore]) {
        this.activePlaces = pg;
        this.plh = plh;
    }
    
    public static def make(pg:PlaceGroup):ResilientStore {
        val plh = PlaceLocalHandle.make[LocalStore](pg, () => {
            new LocalStore(pg.indexOf(here), pg.next(here))
        });
        return new ResilientStore(pg, plh);
    }
    
    public def getVirtualPlaceId() = activePlaces.indexOf(here);
    
    public def getActivePlaces() = activePlaces;

    private def getMaster(p:Place) = activePlaces.prev(p);

    private def getSlave(p:Place) = activePlaces.next(p);

    public def updateForChangedPlaces(changes:ChangeDescription):void {
        // Initialize LocalStore at newly active places.
        for (p in changes.addedPlaces) {
            PlaceLocalHandle.addPlace[LocalStore](plh, p, ()=>new LocalStore());
        }

        checkIfBothMasterAndSlaveDied(changes);
        
        recoverMasters(changes);
        
        recoverSlaves(changes);

        activePlaces = changes.newActivePlaces;
    }

    private def checkIfBothMasterAndSlaveDied(changes:ChangeDescription) {
        for (dp in changes.removedPlaces) {
            val slave = changes.oldActivePlaces.next(dp);
            if (changes.removedPlaces.contains(slave)) {
                val virtualId = changes.oldActivePlaces.indexOf(dp);
                throw new Exception("Fatal: both master and slave lost for virtual place["+virtualId+"] ");
            }
        }
    }

    private def recoverMasters(changes:ChangeDescription) {
        val plh = this.plh; // don't capture this in at!
        finish {
            for (newMaster in changes.addedPlaces) {
                val virtualId = changes.newActivePlaces.indexOf(newMaster);
                val slave = changes.newActivePlaces.next(newMaster);
                at (slave) async {
                    val masterState = plh().slaveStore.getMasterState(virtualId);
                    at (newMaster) async {
                        plh().joinAsMaster(virtualId, masterState.data, masterState.epoch);
                    }
                }
            }
        }
    }

    private def recoverSlaves(changes:ChangeDescription) {
        val plh = this.plh; // don't capture this in at!
        finish {
            for (newSlave in changes.addedPlaces) {
                val master = changes.newActivePlaces.prev(newSlave);
                val masterVirtualId = changes.newActivePlaces.indexOf(master);
                at (master) async {
                    val masterState = plh().masterStore.getState(); 
                    at (newSlave) {
                        plh().slaveStore.addMasterPlace(masterVirtualId, masterState.data, new HashMap[String,TransKeyLog](), masterState.epoch);
                    }
                    plh().slave = newSlave;
                }
            }
        }
    }

    public def startLocalTransaction():LocalTransaction {
        assert(plh().virtualPlaceId != -1);
        val placeIndex = activePlaces.indexOf(here);
        return new LocalTransaction(plh, getNextTransactionId(), placeIndex);
    }
    
    /*
    public def startGlobalTransaction(places:PlaceGroup):GlobalTransaction {
        assert(plh().virtualPlaceId != -1);
        return new GlobalTransaction(plh, Utils.getNextTransactionId(), places);
    }
    */
    
    public def getNextTransactionId() {
        val id = plh().masterStore.sequence.incrementAndGet();
        return 100000+id;
    }
    
    public def printStatus() {
       //Console.OUT.println("DS-- " + here + " " + plh().slave);
    }

    /**
     * Get the value of key k in the resilient map.
     */
    public def get(k:String) {
        val trans = startLocalTransaction();
        val v = trans.get(k);
        trans.prepareAndCommit();
        return v;
    }

    /**
     * Associate value v with key k in the resilient map.
     */
    public def set(k:String, v:Cloneable) {
        val trans = startLocalTransaction();
        trans.put(k, v);
        trans.prepareAndCommit();
    }

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public def delete(k:String) {
        val trans = startLocalTransaction();
        trans.delete(k);
        trans.prepareAndCommit();
    }

    public def keySet() {
        val placeIndex = activePlaces.indexOf(here).toString().length();
        val set = new HashSet[String]();
        for (key in plh().masterStore.keySet()) {
            set.add(key.substring(0n, key.length() - placeIndex));
        }
        return set;
    }
}