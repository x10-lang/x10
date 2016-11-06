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

import x10.util.*;
import x10.util.concurrent.Lock;
import x10.compiler.Inline;
import x10.xrx.Runtime;
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;

public class LocalStore {
    private val moduleName = "LocalStore";
    
    public var masterStore:MasterStore = null;
    public var slave:Place;
    
    public var slaveStore:SlaveStore = null;
    public var virtualPlaceId:Long = -1; //-1 means a spare place
    
    public def this(virtualPlaceId:Long, slave:Place) {
        this.virtualPlaceId = virtualPlaceId;
        this.slave = slave;
        masterStore = new MasterStore(virtualPlaceId);
        slaveStore = new SlaveStore();
    }

    /* used to initialize elastically added or spare places */
    public def this() { }

    /*used when a spare place joins*/
    public def joinAsMaster (virtualPlaceId:Long, data:HashMap[String,HashMap[String,Cloneable]]) {
        this.virtualPlaceId = virtualPlaceId;
        masterStore = new MasterStore(virtualPlaceId, data);
        slaveStore = new SlaveStore();
    }
}