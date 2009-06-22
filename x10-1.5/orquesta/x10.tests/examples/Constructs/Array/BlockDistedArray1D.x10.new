/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;;

/**
* Building arrays distributed accross places using the union-of-distribution approach.
* The performance of this kind of arrays is very poor at this moment.
* @author Tong
  11/29/2006
*/
public class BlockDistedArray1D extends x10Test {
	public const SIZE: int = 5; 
	public const N_PLACES: int = place.MAX_PLACES; 
	public const ALLPLACES: dist = distmakeUnique();
        public def run(): boolean = {
        	var D: dist{rank==1} = Dist.makeConstant([0..SIZE-1], place.factory.place(0)); //The dep type constraint should not be enforced here. 
        	for(var i: int = 1;i<N_PLACES;i++) D=D||(Dist.makeConstant([i*SIZE..(i+1*SIZE-1)], place.factory.place(i)));
        	final val intArray: Array[int] = new Array[int](D, (var point [i]: point): int => {return i;}); 
         	final val dblArray: Array[double] = new Array[double](D, (var point [i]: point): double => {return i*0.1;});
        	finish ateach (val p: point[i] in ALLPLACES) for (val (j): point in intArray|here) dblArray(j)+=intArray(j); 
    			
	    return true;
	}
	
	public static def main(var args: Rail[String]): void = {
		new BlockDistedArray1D().execute();
	}

}
