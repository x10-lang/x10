package x10.util;

public abstract class MapSet[T] extends AbstractCollection[T] implements Set[T] {
    val map: Map[T,boolean];
    
    public def this(map: Map[T,boolean]) { this.map = map; }
  
    public def size(): Int = map.keySet().size();
    public def contains(v: T): Boolean = map.containsKey(v);
    
    public def add(v: T): Boolean = map.put(v, true) == null;
    public def remove(v: T): Boolean = map.remove(v) != null;
    public def clear(): Void = map.clear();    
}
