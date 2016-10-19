package x10.util.resilient.localstore;

import x10.util.HashSet;
import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.concurrent.SimpleLatch;
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;
import x10.util.concurrent.Lock;

public class SlaveStore {
    private val moduleName = "SlaveStore";
    
    private val mastersMap:HashMap[Long,MasterState]; // master_virtual_id, master_data
    private transient val lock:Lock = new Lock();
    
    public def this() {
        mastersMap = new HashMap[Long,MasterState]();
    }
    
    public def addMasterPlace(masterVirtualId:Long, masterData:HashMap[String,Cloneable], transLog:HashMap[String,TransKeyLog], masterEpoch:Long) {
        try {
            lock.lock();
            mastersMap.put(masterVirtualId, new MasterState(masterData,masterEpoch));
            applyChangesLockAcquired(masterVirtualId, transLog, masterEpoch);
        }
        finally {
            lock.unlock();
        }
    }
    
    public def getMasterState(masterVirtualId:Long):MasterState {
        try {
            lock.lock();
            return mastersMap.getOrThrow(masterVirtualId);
        }
        finally {
            lock.unlock();
        }
    }
    
    //Store pending transaction to be ready for commit or rollback
    public def addPendingTransaction(masterVirtualId:Long, transId:Long, transLog:HashMap[String,TransKeyLog], masterEpoch:Long) {
    try {
            lock.lock();
            var masterState:MasterState = mastersMap.getOrElse(masterVirtualId, null);
            if (masterState == null) {
            masterState = new MasterState(new HashMap[String,Cloneable](), masterEpoch);
                mastersMap.put(masterVirtualId, masterState);
            }
            masterState.pendingTrans.put(transId, transLog);
        }
        finally {
            lock.unlock();
        }
    }
    
    
    public def getPendingTransactions(masterVirtualId:Long):HashSet[Long] {
    val set = new HashSet[Long]();
    try {
            lock.lock();
            val masterState = mastersMap.getOrThrow(masterVirtualId);
            val iter = masterState.pendingTrans.keySet().iterator();
            while (iter.hasNext()) {
            val transId = iter.next();
            set.add(transId);
            }
        }
        finally {
            lock.unlock();
        }
    return set;
    }
    
    public def handlePendingTransactions(masterVirtualId:Long, transactions:HashMap[Long,Boolean]) {
        try {
            lock.lock();
            val masterState = mastersMap.getOrThrow(masterVirtualId);
            val iter = transactions.keySet().iterator();
            while (iter.hasNext()) {
                val transId = iter.next();
                val commit =  transactions.getOrThrow(transId);
                if (commit) {
                    commitLockAcquired(masterVirtualId, transId, -1);
                }
                else {
                    rollbackLockAcquired(masterVirtualId, transId);
                }
                masterState.pendingTrans.remove(transId);
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    public def rollback(masterVirtualId:Long, transId:Long) {
        try {
            lock.lock();
            rollbackLockAcquired(masterVirtualId, transId);
        }
        finally {
            lock.unlock();
        }
    }
    
    private def rollbackLockAcquired(masterVirtualId:Long, transId:Long) {
        val masterState = mastersMap.getOrThrow(masterVirtualId);
        masterState.pendingTrans.remove(transId);
    }
    

    
    public def commit(masterVirtualId:Long, transId:Long, masterEpoch:Long) {
        try {
            lock.lock();
            commitLockAcquired(masterVirtualId, transId, masterEpoch);
        }
        finally {
            lock.unlock();
        }
    }
    
    
    private def commitLockAcquired(masterVirtualId:Long, transId:Long, masterEpoch:Long) {
        val masterState = mastersMap.getOrThrow(masterVirtualId);
        val transLog = masterState.pendingTrans.getOrThrow(transId);
        applyChangesLockAcquired(masterVirtualId, transLog, masterEpoch);
        masterState.pendingTrans.remove(transId);
    }
    
    
    /*The master is sure about commiting these changes, go ahead and apply them*/
    private def applyChangesLockAcquired(masterVirtualId:Long, transLog:HashMap[String,TransKeyLog], masterEpoch:Long) {
        var state:MasterState = mastersMap.getOrElse(masterVirtualId, null);
        if (state == null) {
            state = new MasterState(new HashMap[String,Cloneable](), masterEpoch);
            mastersMap.put(masterVirtualId, state);
        }
        val data = state.data;
        val iter = transLog.keySet().iterator();
        while (iter.hasNext()) {
            val key = iter.next();
            val log = transLog.getOrThrow(key);
            if (log.readOnly())
                continue;
            if (log.isDeleted()) 
                data.remove(key);
            else
                data.put(key, log.getValue());
        }
        state.epoch = masterEpoch;
    }
}

class MasterState {
    public var epoch:Long;
    public var data:HashMap[String,Cloneable];
    public val pendingTrans = new HashMap[Long,HashMap[String,TransKeyLog]]();
   
    public def this(data:HashMap[String,Cloneable], epoch:Long) {
        this.data = data;
        this.epoch = epoch;
    }
}