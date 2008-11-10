package x10.util;

public interface List[T] extends Collection[T], Indexed[T], Settable[Int,T] {
  public def indices(): List[Int];
  
  public def addBefore(i: Int, T): Void;
  public def removeAt(i: Int): T;
  public def indexOf(T): Int;
  public def lastIndexOf(T): Int;
  public def indexOf(Int, T): Int;
  public def lastIndexOf(Int, T): Int;
  public def iterator(): ListIterator[T];
  public def iteratorFrom(i: Int): ListIterator[T];
  public def subList(fromIndex: Int, toIndex: Int): List[T];
  
  public def removeFirst(): T;
  public def removeLast(): T;
  public def getFirst(): T;
  public def getLast(): T;

  public def reverse(): Void;
  
  public def sort() {T <: Comparable[T]} : Void;
  public def sort(cmp: (T,T)=>Int): Void;
  // public def sort(lessThan: (T,T)=>Boolean): Void;
}
