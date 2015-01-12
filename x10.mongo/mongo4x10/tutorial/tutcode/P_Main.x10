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
import x10.mongo.yak.YakUtil; 
import x10.mongo.yak.YakMap; 

public class P_Main {
  static val y = YakUtil.it();
  public static def main(argv:Array[String](1)) {
    val setinc = y.set("x",0).inc("n",1);
    val setset = y.set("x",0).set("n",1);
    Runtime.println("setinc = " + setinc);
    Runtime.println("setset = " + setset);
    Runtime.println("lt: " + y.lt("x", 1));
  }
}
