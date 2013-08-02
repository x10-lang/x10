package futuresched.benchs.swithwaterman;

import x10.util.concurrent.Lock;
import x10.util.HashMap;

public class ConcurrentHashMap[K, V] {
  
  val lock = new Lock(); 
  val map = new HashMap();
  
  //public def this() {
  //}

  public def get(k: K): V {
    lock.lock();
    val v = map.get();
    lock.unlock();
    return v();
  }
  
  public def put(k: K, v: V): V {
    lock.lock();
    val v = map.put(k, v);
    lock.unlock();
    return v();
  }
  
  public def containts(k: K): Boolean {
    lock.lock();
    val b = (map.get(k)() != null);
    lock.unlock();
    return b; 
  }
  
  public def putIfAbsent(k: K, v: V): V {
    var r: Box[v];
    lock.lock();
    val v = map.get(k, v);
    if (v() == null) {
      r = put(k, v);
    } else {
      r = v;
    }
    lock.unlock();
    return r();
  }
   
}


