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
 */

public class FlattenArray4 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenArray4 = {
        a = Array.make[int](([1..10, 1..10] as Region)->here, ((i,j): Point) => { return i+j;});
    }

    def m(var x: int): int = {
        return x;
    }

    public def run(): boolean = {
        var x: int = m(a(1, 1)); // being called in a method to force flattening.
        var y: int = m(a(2, 2));
        return x+y==1+1+2+2;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenArray4().execute();
    }
}
