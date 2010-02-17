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
 * @author Tong 11/29/2006
 */

public class BlockDistedArray1D_Dep extends x10Test {

    const SIZE = 5; 
    const N_PLACES = Place.MAX_PLACES; 
    const ALLPLACES = Dist.makeUnique();

    public def run(): boolean = {

        var D: Dist(1);
        D = Dist.makeConstant(0..SIZE-1, Place.place(0));

        for(var i: int = 1;i<N_PLACES;i++) 
            D=D||(Dist.makeConstant(i*SIZE..(i+1*SIZE-1), Place.place(i)));

        val intArray: Array[int](1) = Array.make[int](D, ((i): Point) => i);
        val dblArray: Array[double](1) = Array.make[double](D, ((i): Point)=> i*0.1);

        finish ateach (val p(i): Point in ALLPLACES) 
            for (val (j): Point in intArray|here) 
                dblArray(j) +=intArray(j); 
                    
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new BlockDistedArray1D_Dep().execute();
    }

}
