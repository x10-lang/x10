/* Current test harness gets confused by packages, but it would be in package Clocks_For_Spocks;
*/

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

// file Clocks line 152
class Clockuser {
 def example() {
val c: Clock = Clock.make();
}}

class Hook {
   def run():Boolean = true;
}


public class Clocks2 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Clocks2().execute();
    }
}    
