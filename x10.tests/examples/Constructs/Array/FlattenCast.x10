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

public class FlattenCast extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenCast = {
        a = Array.make[int]([1..10, 1..10]->here, (p(i,j): Point) => i+j);
    }

    def m(var x: int): int = {
        return x;
    }

    public def run(): boolean = {
        val x =  m(a(1, 1)) as Double; // being called in a method to force flattening.
        return 2==x;
    }
    
    public static def main(var args: Rail[String]): void = {
        new FlattenCast().execute();
    }
}
