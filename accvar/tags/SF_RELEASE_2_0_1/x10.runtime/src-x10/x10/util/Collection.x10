package x10.util;

/** A mutable collection. */
public interface Collection[T] extends Container[T] {
  public def add(T): Boolean;
  public def remove(T): Boolean;
  public def addAll(Container[T]!): Boolean;
  public def retainAll(Container[T]!): Boolean;
  public def removeAll(Container[T]!): Boolean;
  public def addAllWhere(Container[T]!, (T) => Boolean): Boolean;
  public def removeAllWhere((T) => Boolean): Boolean;
  public def clear(): Void;
  public def clone(): Collection[T]!;
}
