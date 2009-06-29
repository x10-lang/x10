/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for array reference flattening. Checks that after flattening
 * the variable x and y can still be referenced, i.e. are not 
 * declared within local blocks.
  
 * To check that this test does what it was intended to, examine
 * the output Java file. It should have a series of local variables
 * pulling out the subters of m(a[1,1]).
 */
 
public class FlattenFutureCall extends x10Test {

    val a: Array[int](2);

    public def this(): FlattenFutureCall = {
        a = Array.make[int](([1..10, 1..10] as Region)->here, ((i,j): Point): int => { return i+j;});
    }
    
    public def run(): boolean = {
        var x: boolean = (future(a.dist(1, 1)){ true}).force();
        return x;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenFutureCall().execute();
    }
}
