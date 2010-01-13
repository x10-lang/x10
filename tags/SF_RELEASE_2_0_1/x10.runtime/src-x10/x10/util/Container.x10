package x10.util;

/** A mutable or immutable collection. */
public interface Container[+T]  extends Iterable[T] {
  public def size(): Int;
  public def isEmpty(): Boolean;
  public def contains(T): Boolean;
  public def toValRail(): ValRail[T];
  public def toRail(): Rail[T];
  public def containsAll(Container[T]): Boolean;
  public def clone(): Container[T];
}
