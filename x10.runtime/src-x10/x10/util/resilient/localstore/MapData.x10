package x10.util.resilient.localstore;

import x10.util.concurrent.SimpleLatch;
import x10.util.HashMap;

public class MapData {
	val data:HashMap[String,Cloneable];
	val lock:SimpleLatch;

    public def this() {
    	data = new HashMap[String,Cloneable]();
    	lock = new SimpleLatch();
    }
    
    public def this(data:HashMap[String,Cloneable]) {
    	this.data = data;
    	lock = new SimpleLatch();
    }
    
    public def getCopy(key:String):Cloneable {
        return get(key, true);
    }
    
    public def getNoCopy(key:String):Cloneable {
    	return get(key, false);
    }
    
    private def get(key:String, copy:Boolean):Cloneable {
        val value = data.getOrElse(key, null);
        if (value != null) {
        	return copy? value.clone(): value;            
        }
        else
        	return null;
    }
    
    public def commit(transId:Long, transLog:HashMap[String,TransKeyLog]) {
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
    }
    
    public def keySet() = data.keySet();
    
}