/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Building arrays distributed accross places using the union-of-distribution approach.
 * The performance of this kind of arrays is very poor at this moment.
 * @author Tong  11/29/2006
 */

public class BlockDistedArray1D extends x10Test {

    public const SIZE: int = 5; 
    public const N_PLACES: int = Place.MAX_PLACES; 
    public const ALLPLACES: Dist = Dist.makeUnique();

    public def run(): boolean = {
        var D: Dist{rank==1} = Dist.makeConstant([0..SIZE-1], Place.place(0)); //The dep type constraint should not be enforced here. 
        for(var i: int = 1;i<N_PLACES;i++) 
        	D=D||(Dist.makeConstant([i*SIZE..(i+1*SIZE-1)], Place.place(i)));
        val Df = D;
        val intArray: Array[int](Df) = Array.make[int](Df, ((i):Point): int => {return i;}); 
        val dblArray: Array[double](Df) = Array.make[double](Df, ((i): Point): double => {return i*0.1;});
        finish 
           ateach (p(i): Point in ALLPLACES) 
              for (val (j): Point in intArray|here) 
            	  dblArray(j)+=intArray(j); 
        return true;
    }
    
    public static def main(args: Rail[String]) {
        new BlockDistedArray1D().execute();
    }

}
