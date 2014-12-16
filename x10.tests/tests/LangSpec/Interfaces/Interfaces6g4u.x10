/* Current test harness gets confused by packages, but it would be in package Interfaces6g4u;
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
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



public class Interfaces6g4u extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interfaces6g4u().execute();
    }


// file Interfaces line 101
 static interface Star{}
 static interface Dog{}
 static class Sirius implements Dog, Star{}
 static class Lassie implements Dog, Star{}

 static class Hook {
   def run():Boolean = true;
}

}
