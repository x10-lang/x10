package x10.util.resilient.localstore;

import x10.util.HashSet;
import x10.util.HashMap;

public class ResilientNativeMap (name:String, plh:PlaceLocalHandle[LocalStore]) {
    /**
     * Get the value of key k in the resilient map.
     */
    public def get(k:String) {
        val trans = startLocalTransaction();
        val v = trans.get(k);
        trans.commit();
        return v;
    }

    /**
     * Associate value v with key k in the resilient map.
     */
    public def set(k:String, v:Cloneable) {
        val trans = startLocalTransaction();
        trans.put(k, v);
        trans.commit();
    }

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public def delete(k:String) {
        val trans = startLocalTransaction();
        trans.delete(k);
        trans.commit();
    }

    public def keySet() = plh().masterStore.getMapData(name).keySet();
    
    public def setAll(data:HashMap[String,Cloneable]) {
    	if (data == null)
    		return;    	
        val trans = startLocalTransaction();
        val iter = data.keySet().iterator();
        while (iter.hasNext()) {
            val k = iter.next();	
            trans.put(k, data.getOrThrow(k));
        }
        trans.commit();
    }
    
    public def startLocalTransaction():LocalTransaction {
        assert(plh().virtualPlaceId != -1);
        val mapData = plh().masterStore.getMapData(name);
        mapData.lock.lock(); // serialize transactions on the same map
        return new LocalTransaction(plh, getNextTransactionId(), name, mapData);
    }
    
    public def getNextTransactionId() {
        val id = plh().masterStore.sequence.incrementAndGet();
        return 100000+id;
    }
    
}