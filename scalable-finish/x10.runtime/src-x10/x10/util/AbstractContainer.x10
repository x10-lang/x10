package x10.util;

/** A mutable or immutable collection. */
public abstract class AbstractContainer[+T] implements Container[T] {
  public abstract def size(): Int;
  
  public def isEmpty(): Boolean = size() == 0;
  
  public abstract def contains(y: T): Boolean;
  public abstract def clone(): Container[T];
  public abstract def iterator(): Iterator[T];
  
  public def toValRail(): ValRail[T] {
      val g = new GrowableRail[T](size());
      for (x: T in this) {
          g.add(x);
      }
      return g.toValRail();
  }
      
  public def toRail(): Rail[T] {
      val g = new GrowableRail[T](size());
      for (x: T in this) {
          g.add(x);
      }
      return g.toRail();
  }
  
  public def containsAll(c: Container[T]): Boolean {
      for (x: T in c) {
          if (! contains(x))
              return false;
      }
      return true;
  }
}
