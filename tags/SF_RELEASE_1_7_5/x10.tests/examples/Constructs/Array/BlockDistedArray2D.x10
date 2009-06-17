/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Building arrays distributed accross places using the union-of-distribution approach.
 * The performance of this kind of arrays is very poor at this moment.
 * @author Tong 11/29/2006
 */

public class BlockDistedArray2D extends x10Test {

    public const SIZE: int = 5; 
    public const N_PLACES: int = Place.MAX_PLACES; 
    public const ALLPLACES: Dist = Dist.makeUnique();

    public def run(): boolean = {
        var D: Dist{rank==2} = Dist.makeConstant([0..SIZE-1, 0..SIZE-1], Place.place(0)); //The dep type constraint should not be enforced here.
        for(var i: int = 1;i<N_PLACES;i++) D=D||(Dist.makeConstant([0..SIZE-1, i*SIZE..(i+1*SIZE-1)], Place.place(i)));
        val intArray: Array[int](D) = Array.make[int](D, ((i,j): Point): int => {return i+j;}); 
        val dblArray: Array[double](D) = Array.make[double](D, ((i,j):Point): double => {return (i+j)*0.1;});
        finish ateach ((i): Point in ALLPLACES) for ((j,k): Point in intArray|here) dblArray(j, k)+=intArray(j, k); 
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new BlockDistedArray2D().execute();
    }

}
