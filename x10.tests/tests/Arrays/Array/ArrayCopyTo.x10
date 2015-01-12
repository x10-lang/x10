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

/**
 * Simple tests for copyTo/copyFrom functionality
 */
public class ArrayCopyTo extends x10Test {
    static R1 = Region.make(20,100);
    static R2:Region(2) = Region.make(0..9, 0..9);

    public def run() {
       val localA = new Array[long](R1,(p:Point)=>p(0));
       val localB = new Array[long](R2,(p:Point(2))=>10*p(1)+p(0));
       val remoteA = at (Place.places().next(here)) new RemoteArray[long](new Array[long](R1));
       val remoteB = at (Place.places().next(here)) new RemoteArray[long](new Array[long](R2));
       val finalA = new Array[long](R1);
       val finalB = new Array[long](R2);
       var fail:boolean = false;
       
       finish {
         Array.asyncCopy(localA, 0L, remoteA, 0L, 81L);
         Array.asyncCopy(localB, remoteB);
       }
       finish {
         Array.asyncCopy(remoteA, 0L, finalA, 0L, 81L);
         Array.asyncCopy(remoteB, finalB);
       }
        
       for (p in localA) {
         if (localA(p) != finalA(p)) {
             Console.OUT.println("Expected to find "+localA(p)+" at "+p+" but found "+finalA(p));
             fail = true;
         }
       }

       for (p in localB) {
         if (localB(p) != finalB(p)) {
             Console.OUT.println("Expected to find "+localB(p)+" at "+p+" but found "+finalB(p));
             fail = true;
         }
       }

       return !fail;    
    }

    public static def main(var args: Rail[String]) {
        new ArrayCopyTo().execute();
    }

}
