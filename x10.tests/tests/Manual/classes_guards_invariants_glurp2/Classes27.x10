/* Current test harness gets confused by packages, but it would be in package classes_guards_invariants_glurp2;
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

// file Classes line 1798
 class Pt(x:Int, y:Int){}
class Line(a:Pt, b:Pt{a != b}) {}

class Hook {
   def run():Boolean = true;
}


public class Classes27 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes27().execute();
    }
}    
