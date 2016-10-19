package x10.util.resilient.localstore;

import x10.util.HashSet;
import x10.util.HashMap;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicLong;
import x10.compiler.Ifdef;
import x10.xrx.Runtime;

/*Assumption: no local conflicts*/
public class MasterStore {
    private val moduleName = "MasterStore";
    public var epoch:Long = 1;
    private val lock = new Lock();
    private val data:HashMap[String,Cloneable];
    private val virtualPlaceId:Long;
    
    public val committedTrans = new HashSet[Long]();
    public val rolledbackTrans = new HashSet[Long]();
    
    //used for original active places joined before any failured
    public def this(virtualPlaceId:Long) {
        this.virtualPlaceId = virtualPlaceId;
        this.data = new HashMap[String,Cloneable]();
    }
    
    //used when a spare place is replacing a dead one
    public def this(virtualPlaceId:Long, data:HashMap[String,Cloneable], epoch:Long) {
        this.virtualPlaceId = virtualPlaceId;
        this.data = data;
        this.epoch = epoch;
    }
    
    public def getCopy(key:String):Cloneable {
        return get(key, true);
    }
    
    public def getNoCopy(key:String):Cloneable {
    return get(key, false);
    }
    
    private def get(key:String, copy:Boolean):Cloneable {
        try {
            lock.lock();
            val value = data.getOrElse(key, null);
            if (value != null) {
            return copy? value.clone(): value;            
            }
            else
            return null;
        }
        finally {
            lock.unlock();
        }
    }
    
    public def rollback(transId:Long) {
     try {
             lock.lock();
             rolledbackTrans.add(transId);
         }
         finally {
             lock.unlock();
         }
    }
    
    public def commit(transId:Long, transLog:HashMap[String,TransKeyLog]) {
        try {
            lock.lock();
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
            committedTrans.add(transId);
        }
        finally {
            lock.unlock();
        }
    }
    
    public def getTransactionStatus(transId:Long):Long {
        try {
            lock.lock();
            if (committedTrans.contains(transId))
                return Constants.TRANS_STATUS_COMMITTED;
            else if (rolledbackTrans.contains(transId))
                return Constants.TRANS_STATUS_ROLLEDBACK;
            else
                return Constants.TRANS_STATUS_UNFOUND;
        }
        finally {
            lock.unlock();
        }
    }
    
    public def getState():MasterState {
        try {
            lock.lock();
            return new MasterState(data, epoch);
        }
        finally {
            lock.unlock();
        }
    }
}
