/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures byte arrays are implemented.
 */
public class Array3Byte extends x10Test {

    public def run(): boolean = {
        val r  = [1..10, 1..10] as Region;
        val ia  = Array.make[Byte](r, (x:Point)=>(0 as Byte));
    
        ia(1, 1) = 42 as Byte;
        return (42 == ia(1, 1));
    }

    public static def main(var args: Rail[String]): void = {
        new Array3Byte().execute();
    }
}
