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


/**
 * Check that the types that ought to be inferred for constructors
 * from property() statements are in fact inferred
 *
 * @author bard 5/2010
 */


public class ConstructorsWithInferredTypes extends x10Test {

    public def run(): boolean {
       val s0 : Spot{x==0n} = new Spot();
       val s1 : Spot{x==1n} = new Spot(1n);
       val q0 : Spot2{x==0n} = new Spot2();
       val q1 : Spot2{x==1n} = new Spot2(1n);
       // If this compiles then it's fine.
       return true;
    }

    public static def main(Rail[String]) {
        new ConstructorsWithInferredTypes().execute();
    }
}

class Spot(x:Int) {
   def this():Spot{self.x==0n} {property(0n);}
   def this(xx:Int):Spot{self.x==xx} {property(xx);}
}
class Spot2(x:Int) {
   def this() {property(0n);}
   def this(xx:Int) {property(xx);}
}
