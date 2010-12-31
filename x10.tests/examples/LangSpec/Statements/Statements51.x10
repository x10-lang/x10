/* Current test harness gets confused by packages, but it would be in package statements51;
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



public class Statements51 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements51().execute();
    }


// file Statements line 197
 abstract static  class Example {
 abstract def phase1(String):void;
 abstract def phase2(String):void;
 abstract def phase3(String):void;
 abstract def suitable_for_phase_2(String):Boolean;
 abstract def suitable_for_phase_3(String):Boolean;
 def example(filename: String) {
multiphase: {
  if (!exists(filename)) break multiphase;
  phase1(filename);
  if (!suitable_for_phase_2(filename)) break multiphase;
  phase2(filename);
  if (!suitable_for_phase_3(filename)) break multiphase;
  phase3(filename);
}
// Now the file has been phased as much as possible
}

 static class Hook {
   def run():Boolean = true;
}

}