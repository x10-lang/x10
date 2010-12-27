/* Current test harness gets confused by packages, but it would be in package Types_Inferred_By_Phone;
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

// file Types line 1748
class Spot(x:Int) {
  def this() {property(0);}
  def this(xx: Int) { property(xx); }
}
class Confirm{
 static val s0 : Spot{x==0} = new Spot();
 static val s1 : Spot{x==1} = new Spot(1);
}

class Hook {
   def run():Boolean = true;
}


public class Types52 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types52().execute();
    }
}    
