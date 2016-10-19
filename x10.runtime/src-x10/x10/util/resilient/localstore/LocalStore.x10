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
    
    public def this(spare:Long, slaveMap:Rail[Long]) {
        val activePlaces = Place.numPlaces() - spare;
        if (here.id < activePlaces){
            virtualPlaceId = here.id;
            slave = Place(slaveMap(virtualPlaceId));
            masterStore = new MasterStore(virtualPlaceId);
            slaveStore = new SlaveStore();
        }
    }

    /*used when a spare place joins*/
    public def joinAsMaster (virtualPlaceId:Long, data:HashMap[String,Cloneable], epoch:Long) {
        this.virtualPlaceId = virtualPlaceId;
        masterStore = new MasterStore(virtualPlaceId, data, epoch);
        slaveStore = new SlaveStore();
    }      
    
    /*used when a spare place joins*/
    public def joinAsSlave (masterVirtualPlaceId:Long, masterData:HashMap[String,Cloneable], masterEpoch:Long) {
        assert(slaveStore != null);
        slaveStore.addMasterPlace(masterVirtualPlaceId, masterData, new HashMap[String,TransKeyLog](), masterEpoch);
    }
    
}