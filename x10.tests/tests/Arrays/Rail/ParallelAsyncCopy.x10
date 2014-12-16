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

import harness.x10Test;
import x10.io.Console;

/**
 * More complicated tests for Rail.asyncCopy functionality, 
 * running multiple asyncs in parallel to ensure the correct
 * values get into the correct destination.
 */
public class ParallelAsyncCopy extends x10Test {

    public def run():Boolean {
      if (Place.numPlaces() == 1) {
          Console.OUT.println("This test requires at least 2 places");
          return false; // fail
      }
      
      val result:Cell[Boolean] = new Cell[Boolean](true); // assume pass
      val gr_result:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](result);
      
      val o = new ParallelAsyncCopy();
      
      finish for (p in Place.places()) at (p) async {
          var pass:Boolean = true;
          finish {
              for (i in 0..10) {
                  async { 
                      if (!o.doTest()) at (gr_result.home) gr_result().set(false); // fail
                  }
              }
          }
      }
      return result();
   }
  
   private def doTest():Boolean {
       val sz: Long = 50000;
       val rail1 = new Rail[Double](sz, (i:Long)=>(1 as Double));
       val grail1: GlobalRail[Double] = new GlobalRail(rail1);
       val rail2 = new Rail[Double](sz, (i:Long)=>(2 as Double));
       val grail2: GlobalRail[Double] = new GlobalRail(rail2);
   
       at (Place.places().next(here)) {
           val local = new Rail[Double](sz, (i:Long)=>(3 as Double));
           finish Rail.asyncCopy(local, 0, grail1, 0, sz);
       }
   
       at (Place.places().next(here)) {
           val local = new Rail[Double](sz, (i:Long)=>(4 as Double));
           finish Rail.asyncCopy(local, 0, grail2, 0, sz);
       }
   
       for (i in 0..(sz-1)) {
           if (!rail1(i).equals(3 as Double) || !rail2(i).equals(4 as Double)) {
               Console.OUT.println("Mismatch detected at position "+i+" of "+sz+": "+rail1(i)+", "+rail2(i));
   		       return false;
           }
       }
       return true;
   }
   
   public static def main(var args: Rail[String]) {
       new ParallelAsyncCopy().execute();
   }

}


