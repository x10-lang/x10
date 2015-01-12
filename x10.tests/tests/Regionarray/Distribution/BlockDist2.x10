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
import x10.regionarray.*;

public class BlockDist2 extends x10Test {
  public static def main(Rail[String]){
     new BlockDist2().execute();
  }

  static def str(d:Dist):String {
    var s : String = "";
    for(p in d.region) 
      s += " " + d(p).id;
    return s;
  }

  static def actualSize(R: Region):Long {
    var n : Long = 0;
    for(p in R) n++;
    return n;
  }

  public def run():boolean {
     for (n in 10..100) {
        val R = Region.make(1,n);
        val D = Dist.makeBlock(R);
        val M = Place.numPlaces();
        val l : long = (n / M) as long; // Minimum number in a place.
        var prev : Long = -1; 
        for (p in Place.places()) {
          val atP =  D.get(p);
          val np = actualSize(atP);
          chk(np == atP.size(), "Size of " + atP + " really: " + np + " but .size()=" + atP.size());
          
          chk(np == l || np == l+1, 
            "number at p test for p=" + p + " + n=" + n
            + " -- expects l=" + l + " or " + (l+1) 
            + ", but found np=" + np
            + "\n D= " + str(D)
            + "\n atP = " + atP
            );
          if (prev != -1L) {
            if (prev == l) chk(np == l, "big blocks before small blocks for p=" + p + ", n=" + n);
          }
          prev = np;
        }
     }
     return true;
  }
}
