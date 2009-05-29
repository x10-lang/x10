package x10.util;

/** A mutable collection. */
public abstract class AbstractCollection[T] extends AbstractContainer[T] implements Collection[T] {
  public abstract def add(T): Boolean;
  public abstract def remove(T): Boolean;
  
  public def addAll(c: Container[T]): Boolean = addAllWhere(c, (T) => true);
  public def retainAll(c: Container[T]): Boolean = removeAllWhere((x:T) => !c.contains(x));
  public def removeAll(c: Container[T]): Boolean = removeAllWhere((x:T) => c.contains(x));

  public def addAllWhere(c: Container[T], p: (T) => Boolean): Boolean {
      var result: Boolean = false;
      for (x: T in c) {
          if (p(x))
              result |= add(x);
      }
      return result;
  }
  
  public def removeAllWhere(p: (T) => Boolean): Boolean {
     var result: Boolean = false;
     for (x: T in this.clone()) {
         if (p(x))
             result |= remove(x);
     }
     return result;
  }
  
  public def clear(): Void { removeAllWhere((T)=>true); }
  
  public abstract def clone(): Collection[T];
}
