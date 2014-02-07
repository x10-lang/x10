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

public class A_Main {
  public static def main(argv:Array[String](1)) {
    val y = YakUtil.it(); 
    val hello = y.collection("test", "x10tutorial.hello");
    hello.drop();
    // Populate the database
    hello.save(y -<"txt">- "world!");
    hello.save(y -<"txt">- "ll");
    hello.save(y -<"txt">- "o, ");
    hello.save(y -<"txt">- "He");
    // Do a query.
    val cursor = hello.find().sort(y -<"txt">- 1);
    for (rec in cursor) Console.OUT.print(rec.str("txt"));
    Console.OUT.println();
    hello.drop();
  }
}
