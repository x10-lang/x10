/* Current test harness gets confused by packages, but it would be in package triangleExample_partTwo;
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

// file Types line 1171
 struct Position(x: Int, y: Int) {
    def this(x:Int,y:Int){property(x,y);}
    }
 class Line(start: Position,
            end: Position{self != start}) {}

struct Triangle
 (a: Line,
  b: Line{a.end == b.start},
  c: Line{b.end == c.start && c.end == a.start})
 {}

class Hook {
   def run():Boolean = true;
}


public class Types35 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types35().execute();
    }
}    
