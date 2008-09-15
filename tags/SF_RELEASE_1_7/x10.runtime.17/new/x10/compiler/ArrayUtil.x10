/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

public class ArrayUtil {

    public static def get[T](a: Array[T],p: Point(this.rank)) = (a.apply(p) to Box[T]) to T;
    public static def get[T](a: Array[T],i0: int){rank==1} = (a.apply(i0) to Box[T]) to T;
    public static  def get[T](a: Array[T],i0: int, i1: int){rank==2} = (a.apply(i0,i1) to Box[T]) to T;
    public static def get[T](a: Array[T],i0: int, i1: int, i2: int){rank==3} 
    = (a.apply(i0,i1,12) to Box[T]) to T;
    
    
}
