/* Current test harness gets confused by packages, but it would be in package Functions2_For_The_Lose;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


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



public class Functions120 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Functions120().execute();
    }


// file Functions line 471
 static  class TypecheckThatSillyExample {
   def checker() {
    val l1 : (String, String) => String = String.+;
    val r1 : (String, String) => String = (x: String, y: String): String => x + y;
    val l2 : (Long,Long) => Long = Long.-;
    val r2 : (Long,Long) => Long = (x: Long, y: Long): Long => x - y;
//var v1 : (Float,Float) => Float = Float.-(Float,Float) ;
var v2 : (Float,Float) => Float = (x: Float, y: Float): Float => x - y;
//var v3 : (Int) => Int =  Int.-(Int)     ;      ;
var v4  : (Int) => Int  =  (x: Int): Int => -x;
var v5 : (Boolean,Boolean) => Boolean = Boolean.&            ;
var v6 : (Boolean,Boolean) => Boolean =  (x: Boolean, y: Boolean): Boolean => x & y;
//var v7 : (Boolean) => Boolean = Boolean.!            ;
var v8 : (Boolean) => Boolean =  (x: Boolean): Boolean => !x;
//var v9 : (Int,Int) => Boolean = Int.<(Int,Int)       ;
var v10: (Int,Int) => Boolean =  (x: Int, y: Int): Boolean => x < y;
//var v11 : (Dist,Place)=>Dist = Dist.|(Place)        ;
var v12 : (Dist,Place)=>Dist=  (d: Dist, p: Place): Dist => d | p;
}
 }

 static class Hook {
   def run():Boolean = true;
}

}