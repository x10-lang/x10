/* Current test harness gets confused by packages, but it would be in package Arrays_Pointwise_Pointless_Map2;
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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

 import x10.regionarray.*;

public class Arrays490 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Arrays490().execute();
    }


// file Arrays line 1061
 static  class Example{
static def add(da:DistArray[Long], db: DistArray[Long])
    {da.dist==db.dist}
    = da.map(db, (a:Long,b:Long)=>a+b);
}

 static class Hook {
   def run():Boolean = true;
}

}
