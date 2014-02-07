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
 * Test that if a class implements an interface, and the interface specifies a property, 
 * then the class defines the property.
 * @author vj
 */
public class InheritedInvariantMustBeEntailed_MustFailCompile extends x10Test { 

    public static interface Test {l()!=m()} {
        public property l():int;
        public property m():int;
      def put():int;
    }
    
    //  must fail here because the interface invariant is not met
    class Tester  (l:int, m:int) implements Test {
        public property l():int = l;
        public property m():int = m;
      public def this(arg:int):Tester { property(arg,arg); } // ERR
      public def put()=0n;
    }
 
    public def run()=false;
    
    public static def main(Rail[String]) {
      new InheritedInvariantMustBeEntailed_MustFailCompile().execute();
    }
}
