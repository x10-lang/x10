 
package x10.compiler;



public abstract class ClockedRailBase[T] extends Rail[T] {

 public  def this (length: int) {
	super(length);
}

abstract public def  setClocked(i: int, v: T) : void;
abstract public def  getClocked(i: int) : T;
 



}

  
