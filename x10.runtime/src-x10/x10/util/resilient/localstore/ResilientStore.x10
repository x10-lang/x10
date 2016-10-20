package x10.util.resilient.localstore;

import x10.util.Team;
import x10.util.HashSet;
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.concurrent.SimpleLatch;
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;
import x10.util.resilient.iterative.PlaceGroupBuilder;

public class ResilientStore {
    private val moduleName = "ResilientStore";
    private val plh:PlaceLocalHandle[LocalStore];
    private var activePlaces:PlaceGroup;
    private var sparePlaces:ArrayList[Place];
    private var deadPlaces:ArrayList[Place];    
    private val slaveMap:Rail[Long]; //master virtual place to slave physical place
    private val sequence:AtomicLong = new AtomicLong();
    
    private def this(activePlaces:PlaceGroup, plh:PlaceLocalHandle[LocalStore], slaveMap:Rail[Long], sparePlaces:ArrayList[Place]){
        this.activePlaces = activePlaces;
        this.plh = plh;
        this.slaveMap = slaveMap;
        this.sparePlaces = sparePlaces;
    }
    
    public static def make(spare:Long):ResilientStore {
        val activePlaces = PlaceGroupBuilder.excludeSparePlaces(spare);
        val slaveMap = new Rail[Long](activePlaces.size, (i:long) => { (i + 1) % activePlaces.size} );
        val plh = PlaceLocalHandle.make[LocalStore](Place.places(), () => new LocalStore(spare, slaveMap));
        val sparePlaces = new ArrayList[Place]();
        for (var i:Long = activePlaces.size(); i< Place.numPlaces(); i++){
            sparePlaces.add(Place(i));
        }
        return new ResilientStore(activePlaces, plh, slaveMap, sparePlaces);
    }
    
    public def getVirtualPlaceId() = activePlaces.indexOf(here);
    
    public def getActivePlaces() = activePlaces;
    
    public def recoverDeadPlaces():HashMap[Long,Long] {
        val oldPlaceGroup = activePlaces;
        val addedSparePlaces = new HashMap[Long,Long](); // key=readId, value=virtualPlaceId
        val mastersLostTheirSlaves = new ArrayList[Long]();
        val group = new x10.util.ArrayList[Place]();
        var deadCount:Long = 0;
        var allocated:Long = 0;
        var virtualPlaceId:Long = 0;
        for (p in oldPlaceGroup){
            if (p.isDead()){
                deadCount++;
                if (sparePlaces.size() > 0){
                    val sparePlace = sparePlaces.removeAt(0);
                    Console.OUT.println("place ["+sparePlace.id+"] is replacing ["+p.id+"] since it is dead ");
                    group.add(sparePlace);
                    addedSparePlaces.put(sparePlace.id,virtualPlaceId);
                    Console.OUT.println("=========================================================");
                    Console.OUT.println("[         "+sparePlace.id+"       ,        "+virtualPlaceId+"         ]");
                    Console.OUT.println("=========================================================");                    
                    allocated++;
                }
                else
                    throw new Exception("No enough spare places found ...");
                
                //FIXME: there may be more than one
                mastersLostTheirSlaves.add(findMasterVirtualIdGivenSlave(p.id));
            }
            else{
                group.add(p);
            }
            virtualPlaceId++;
        }      
        
        activePlaces = new SparsePlaceGroup(group.toRail());
        
        checkIfBothMasterAndSlaveDied(addedSparePlaces, mastersLostTheirSlaves);
        
        recoverMasters(addedSparePlaces);
        
        recoverSlaves(mastersLostTheirSlaves);
        
        return addedSparePlaces;
    }
    
    private def checkIfBothMasterAndSlaveDied(addedSparePlaces:HashMap[Long,Long], mastersLostTheirSlaves:ArrayList[Long]) {
        val iter = addedSparePlaces.keySet().iterator();
        if (iter.hasNext()) {
            val masterRealId = iter.next();
            val masterVirtualId = addedSparePlaces.getOrThrow(masterRealId);
            if (mastersLostTheirSlaves.contains(masterVirtualId)) {
                throw new Exception("Fatal: both master and slave lost for virtual place["+masterVirtualId+"] ");
            }
        }
    }
    
    private def findMasterVirtualIdGivenSlave(slaveRealId:Long) {
        for (var i:Long = 0; i < activePlaces.size(); i++) {
            if (slaveMap(i) == slaveRealId)
                return i;
        }
        throw new Exception("Fatal error: could not find master for slave at ["+slaveRealId+"]");
    }
    
    private def recoverMasters(addedSparePlaces:HashMap[Long,Long]) {
        val iter = addedSparePlaces.keySet().iterator();
        finish {
            while (iter.hasNext()) {
                val realId = iter.next();
                val virtualId = addedSparePlaces.getOrThrow(realId);
                val slaveRealId = slaveMap(virtualId);
                val slave = Place(slaveRealId);
                assert(!slave.isDead());
                
                recoverSlavePendingTransactions(slave, virtualId);
                
                at (slave) async {
                    val masterState = plh().slaveStore.getMasterState(virtualId);
                    at (Place(realId)) {
                        plh().joinAsMaster (virtualId, masterState.data, masterState.epoch);
                    }
                }
            }
        }
    }
    
    /*
     * Slave data may be inconsistent if the master died between prepare transaction and commit transaction
     * In that case, we must consult another member in the transaction to know what to do with the pending transactions at the slave
     * */    
    private def recoverSlavePendingTransactions(slave:Place, masterVirtualId:Long) {
        val pendingTransactions = at (slave) plh().slaveStore.getPendingTransactions(masterVirtualId);
        val commitMap = new HashMap[Long,Boolean]();
        val iter = pendingTransactions.iterator();
        while (iter.hasNext()) {
            val transId = iter.next();
            ////////////////////////////////////////////////////////////////////////////////////////////////
            //FIXME: now we rely that the current place is always a member in the active places (not a slave)
            val status = plh().masterStore.getTransactionStatus(transId);
            assert (status != Constants.TRANS_STATUS_UNFOUND && status != Constants.TRANS_STATUS_PENDING);
            //////////////////////////////////////////////////////////////////////////////////////////////////
            if (status == Constants.TRANS_STATUS_COMMITTED) {
                commitMap.put(transId, true);
            }
            else {
                commitMap.put(transId, false);
            }
        }
        at (slave) plh().slaveStore.handlePendingTransactions(masterVirtualId, commitMap);
    }
    
    private def recoverSlaves(mastersLostTheirSlaves:ArrayList[Long]) {
        val masterNewSlave = new HashMap[Long,Long](); // key=masterVirtualId, value=slaveRealId
        for (masterVirtualId in mastersLostTheirSlaves) {
            val oldSlaveRealId = Place(slaveMap(masterVirtualId)).id;
            var found:Boolean = false;
            var newSlaveRealId:Long = 0;
            for (var i:Long = 1; i < Place.numPlaces(); i++){
                if (!Place(oldSlaveRealId + i).isDead()) {
                    found = true;
                    newSlaveRealId = Place(oldSlaveRealId + i).id;
                    masterNewSlave.put(masterVirtualId, newSlaveRealId);
                    break;
                }
            }
            if (!found)
                throw new Exception("[Fatal] could not find a new slave");
        }
        
        
        finish {
            val iter = masterNewSlave.keySet().iterator();
            while (iter.hasNext()) {
                val masterVirtualId = iter.next();
                val slaveRealId:Long = masterNewSlave.getOrThrow(masterVirtualId);
                at (activePlaces(masterVirtualId)) {
                    val masterState = plh().masterStore.getState(); 
                    at (Place(slaveRealId)) {
                        plh().slaveStore.addMasterPlace(masterVirtualId, masterState.data, new HashMap[String,TransKeyLog](), masterState.epoch);
                    }
                    plh().slave = Place(slaveRealId);
                }
            }
        }
        
        val iter = masterNewSlave.keySet().iterator();
        while (iter.hasNext()) {
            val masterVirtualId = iter.next();
            val slaveRealId:Long = masterNewSlave.getOrThrow(masterVirtualId);
            slaveMap(masterVirtualId) = slaveRealId;
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
        val id = sequence.incrementAndGet();
        return 100000+id;
    }
    
    public def printStatus() {
       //Console.OUT.println("DS-- " + here + " " + plh().slave);
    }
}