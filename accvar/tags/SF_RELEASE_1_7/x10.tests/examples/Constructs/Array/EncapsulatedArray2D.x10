/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
* Building arrays distributed accross places using the encapsulation approach 
* (2D array of 2D arrays).
* @author Tong
  11/29/2006
*/
public class EncapsulatedArray2D extends x10Test {
	
	static value Wrapper{
		val m_array: Array[double](2);
		def this(var a_array: Array[double](2)): Wrapper = {
			m_array=a_array;
		}
	}
	
        public def run(): boolean = {
        	val size: int = 5;
        	val R = [0..size-1, 0..size-1] to Region;
        	val D  = Dist.makeCyclic(R); 
        	
        	val A = Array.make[Wrapper](D, (point) => 
        	   new Wrapper(Array.make[double](R->here, (point)=>0.0D)));
        
        		
        	//for (int i=0;i<numOfPlaces;i++){	
        	finish ateach (val (i,j): point in D) { 
        		val temp = A(i, j).m_array; 
        		for (val p: point(2) in temp) temp(p)=(i+j to Double);
        	}
    	
	    return true;
	}
	
	public static def main(var args: Rail[String]): void = {
		new EncapsulatedArray2D().execute();
	}

}
