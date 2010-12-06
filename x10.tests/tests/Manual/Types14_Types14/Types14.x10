/* Current test harness gets confused by packages, but it would be in package Types14_Types14;
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

// file Types line 382

interface Named {
   def name():String;
 }
 interface Mobile {
   def move(howFar:Int):void;
 }
 interface Person extends Named, Mobile {}
 interface NamedPoint extends Named, Mobile{}
class KimThePoint implements Person {
   var pos : Int = 0;
   public def name() = "Kim (" + pos + ")";
   public def move(dPos:Int) { pos += dPos; }
}


class Hook {
   def run():Boolean = true;
}


public class Types14 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types14().execute();
    }
}    
