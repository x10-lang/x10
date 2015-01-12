/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.util.*;
public class TypeArgInference2 extends x10Test {
        def choose[T] (a:T, b:T):T = a;

    public def run(): boolean = {
           chk( Set[Int] <: Collection[Int], "pre-1");
           chk( List[Int] <: Collection[Int], "pre-2");
           chk( Sub <: Super, "pre-3");
           val intSet = new HashSet[Int]();
           val stringList = new ArrayList[String]();
           val x = choose(intSet, stringList);
           val xx : Any /*Collection[Any]*/ = x; // cannot be Collection[Any], since we dont support covariance.
           val intList = new ArrayList[Int]();
           val y = choose(intSet, intList);
           val yy : Collection[Int] = y;
           return true;
    }

    public static def main(Rail[String])  {
        new TypeArgInference1().execute();
    }
   private static class Super {}
   private static class Sub extends Super{}

}
