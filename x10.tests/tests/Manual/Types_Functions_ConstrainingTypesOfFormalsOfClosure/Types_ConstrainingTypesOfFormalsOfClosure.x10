/* Current test harness gets confused by packages, but it would be in package Types_Functions_ConstrainingTypesOfFormalsOfClosure;
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

// file Types line 1276
 class IncrElEx {
 static def example()  {
val inc = (r:Array[Int](1), i: Int{i != r.size}) => {
  if (i < 0 || i >= r.size) throw new DoomExn();
  r(i)++;
};
}}
class DoomExn extends Exception{}

class Hook {
   def run():Boolean = true;
}


public class Types_ConstrainingTypesOfFormalsOfClosure extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types_ConstrainingTypesOfFormalsOfClosure().execute();
    }
}    
