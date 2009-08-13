package x10.util;

public abstract class HashSet[T] extends MapSet[T] {
    public def this() { super(new HashMap[T,boolean]()); }
    public def this(sz: int) { super(new HashMap[T,boolean](sz)); }
  
    public incomplete def clone(): HashSet[T];
}