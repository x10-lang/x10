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
        val d1next = d(1).next();
        /*
        Original code: 
        val x =  (a(1,1) as Test{self.home == d1next}) ;
        return true;
        Bard's reaction: 
        WTF? a(1,1) is defined to be here, 
          so it's probably not going to be at here.next(), 
          so this cast should fail, shouldn't it?
        */
        val d1 = d(1);
        val x = a(1,1) as Test{self.home == d1};
        val y = a(1,1) as Test{self.home == here};
        val z = a(1,1) as Test!;
        return true;
    }

    public static def main(Rail[String]) {
        new FlattenPlaceCast().execute();
    }
    
}

