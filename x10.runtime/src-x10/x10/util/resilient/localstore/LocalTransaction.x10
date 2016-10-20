package x10.util.resilient.localstore;

import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.HashSet;
import x10.compiler.Ifdef;
import x10.xrx.Runtime;

public class LocalTransaction (plh:PlaceLocalHandle[LocalStore], id:Long, placeIndex:Long) {
private val moduleName = "LocalTransaction";

    private val transLog:HashMap[String,TransKeyLog] = new HashMap[String,TransKeyLog]();    
    private var preparedToCommit:Boolean = false;
    private var alive:Boolean = true; // the transaction is alive if it can process more puts and gets

    public def put(key:String, newValue:Cloneable):Cloneable {
    assert(alive);
    val copiedValue = newValue.clone();    
        var oldValue:Cloneable = null;    
        val keyLog = transLog.getOrElse(key+placeIndex,null);
        if (keyLog != null) { // key used in the transaction before
            oldValue = keyLog.getValue();
            keyLog.update(copiedValue);
        }
        else {// first time to access this key
            val value = plh().masterStore.getNoCopy(key+placeIndex); // no need to copy, we will overwrite this value any way
            val log = new TransKeyLog(value);
            transLog.put(key+placeIndex, log);
            log.update(copiedValue);
            oldValue = value;
        }
        return oldValue;
    }
    
    
    public def delete(key:String):Cloneable {
    assert(alive);
        var oldValue:Cloneable = null;    
        val keyLog = transLog.getOrElse(key+placeIndex,null);
        if (keyLog != null) { // key used in the transaction before
            oldValue = keyLog.getValue();
            keyLog.delete();
        }
        else {// first time to access this key
            val value = plh().masterStore.getNoCopy(key+placeIndex);// no need to copy, we will delete this value any way
            val log = new TransKeyLog(value);
            transLog.put(key+placeIndex, log);
            log.delete();
            oldValue = value;
        }
        return oldValue;
    }
    
    
    public def get(key:String):Cloneable {
    assert(alive);
        var oldValue:Cloneable = null;
        val keyLog = transLog.getOrElse(key+placeIndex,null);
        if (keyLog != null) { // key used before in the transaction
           oldValue = keyLog.getValue();
        }
        else {
            val value = plh().masterStore.getCopy(key+placeIndex);
            val log = new TransKeyLog(value);
            transLog.put(key+placeIndex, log);
            oldValue = value;
        }
        return oldValue;
    }

    /**
     * Dead slave is fatal
     **/
    public def prepareAndCommit() {
        assert(alive);
        if (isReadOnlyTransaction()){
            preparedToCommit = true;
            alive = false;
            return;
        }
        
        val masterVirtualId = plh().virtualPlaceId;
        plh().masterStore.epoch++;
        val masterEpoch = plh().masterStore.epoch;
        val masterPL = here;
        at (plh().slave) {
            plh().slaveStore.addPendingTransaction(masterVirtualId, id, transLog, masterEpoch);
            plh().slaveStore.commit(masterVirtualId, id, masterEpoch);
        }
        //master commit
        plh().masterStore.commit(id, transLog);
        
        preparedToCommit = true;
        alive = false;
    }
    
    /**
     * Dead slave is fatal
     **/
    public def prepare() {
    assert(alive);
    if (isReadOnlyTransaction()){
        preparedToCommit = true;
        return;
        }
    
    val masterVirtualId = plh().virtualPlaceId;
    val masterEpoch = plh().masterStore.epoch;
    at (plh().slave) {
            plh().slaveStore.addPendingTransaction(masterVirtualId, id, transLog, masterEpoch);
        }
    preparedToCommit = true;
    }
    
    /**
     * Dead slave is ignored
     **/
    public def commit() {
    assert (alive);
    
    if (isReadOnlyTransaction()){
    return;
    }
    
    assert(preparedToCommit); // non-read-only transactions must be prepared first because they involve interactions with the slave
    
        try {
            plh().masterStore.epoch++;
            val masterEpoch = plh().masterStore.epoch;
            val masterVirtualId = plh().virtualPlaceId;
            at (plh().slave) {
                plh().slaveStore.commit(masterVirtualId, id, masterEpoch);
            }
        }
        catch(ex:Exception) {
            //Ignore slave death, because other places who have their slaves active will commit
        }
        
        //master commit
        plh().masterStore.commit(id, transLog);
        alive = false;
    }
    
    /**
     * Dead slave is ignored
     **/
    public def rollback() {
    assert (alive);
        try {
            val masterVirtualId = plh().virtualPlaceId;
            at (plh().slave) {
                plh().slaveStore.rollback(masterVirtualId, id);
            }
        }
        catch(ex:Exception) {            
        //Ignore slave death, because other places who have their slaves active will rollback 
        }
        plh().masterStore.rollback(id);
        alive = false;
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