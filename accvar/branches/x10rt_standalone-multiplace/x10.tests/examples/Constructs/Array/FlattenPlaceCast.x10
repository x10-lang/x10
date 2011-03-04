/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.

import harness.x10Test;

/**
 * In an earlier implementation this would give a t0 not reachable error.
 */
public class FlattenPlaceCast extends x10Test {

    val a: Array[Test](2);
    val d: Array[Place](1);

    public def this() {
        a = Array.make[Test](([1..10, 1..10] as Region) -> here, (Point)=>new Test());
        d = Array.make[Place](1..10 -> here, (Point)=>here);
    }
   
    static class Test {};

    public def run():boolean  = {
        val x =  (a(1,1) as Test{self.home == d(1).next()}) ;
        return true;
    }

    public static def main(Rail[String]) {
        new FlattenPlaceCast().execute();
    }
    
}

