/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
import x10.mongo.yak.YakUtil; 
import x10.mongo.yak.YakMap; 

public class B_Main {
  static val y = YakUtil.it();
  public static def main(argv:Array[String](1)) {
    making_new_maps();
    adding_maps();
    getting_stuff_out_of_maps();
    making_queries();
    making_updates();
  }

  static def making_new_maps(){
    Runtime.println("=== Making New Maps ===");
    val a = y -< "a" >- 11; 
    Runtime.println("a=" + a);
    val abc = y -<"a">- 11
                -<"b">- 22
                -<"c">- 33;
    Runtime.println("abc=" + abc);
    val ab2 = y -<"a">- 11
                -<"b">- (y -<"bx">- 44 -<"by">- 55);
    Runtime.println("ab2=" + ab2);
    // Notice that -< modifies its argument! 
    val b = a -< "b" >- 22;
    Runtime.println("Now, a=" + a + " and b=" + b);
    // Use 'dup()' to copy
    val c = a.dup() -< "c" >- 33;
    Runtime.println("After dup, a=" + a + " and b=" + b + " and c=" + c);
    Runtime.println("=== End of Making New Maps ===");
  }

  static def adding_maps(){
    Runtime.println("=== Adding Maps ===");
    val a = y -<"a">- 10;
    val b = y -<"b">- 20;
    val c = a + b;
    Runtime.println("First, a = " + a + "; b = " + b + "; c = " + c);
    a.put("a", "new-a");
    Runtime.println("Second a = " + a + "; b = " + b + "; c = " + c);
    Runtime.println("=== End of Adding Maps ===");
  }
  
  static def getting_stuff_out_of_maps(){
    Runtime.println("=== Getting stuff out of maps ===");
    val m = y -<"in">- 3
              -<"lo">- 123456789L
              -<"db">- 1.2e-3
              -<"bo">- true
              -<"st">- "stringy!"
              -<"ma">- (y -<"a">-1 
                          -<"b">-true 
                          -<"c">- (y -<"x">- 11));
    val oin : Int = m.int("in");
    val olo : Long = m.long("lo");
    val odb : Double = m.double("db");
    val obo : Boolean = m.bool("bo");
    val ost : String = m.str("st");
    val m1  : YakMap = m / "ma";
    val m2  : YakMap = m / "ma" / "c";
    Runtime.println("in = " + oin + ", lo = " + olo + ", db = " + odb + ", st = " + ost);
    Runtime.println("m1 = " + m1);
    Runtime.println("m2 = " + m2);
    Runtime.println("=== End of getting stuff out of maps ===");
  }

  static def making_queries(){
    Runtime.println("=== Making Queries ===");
    val q1 = y.eq("a", 1);
    val q2 = y.eq("a", 1).gt("b", 2);
    val q3 = y.exists("a").mod("c", 2, 0);
    Runtime.println("q1="+q1 + "\nq2=" + q2 + "\nq3=" + q3);    
    Runtime.println("=== End Of Making Queries ===");
  }
    
  static def making_updates() {
    Runtime.println("=== Making Updates ===");
    val u1 = y.set("a", "aye").inc("b", 1);
    val u2 = y.set("a", "aa").set("b", "bb").inc("c", 1).inc("d", 2);
    Runtime.println("u2=" + u2);
    Runtime.println("=== End Of Making Updates ===");
  }
}
