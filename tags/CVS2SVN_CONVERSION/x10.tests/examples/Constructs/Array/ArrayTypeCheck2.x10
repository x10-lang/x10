/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author vj
 */

public class ArrayTypeCheck2 extends x10Test {

    public def run(): boolean = {

        val two = 2;
        
        var a1: Array[int](two) = Array.make[int](Dist.makeConstant([0..2, 0..3], here), 
         ((i): Point)=> i);
        
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayTypeCheck2().execute();
    }
}
