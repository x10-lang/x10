/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * 
 * Testing that thisType is correctly updated and propagated into the code to check a
 * method call.
 * 
 * @author vj
 */
public class CheckThisTypeInCall extends x10Test {
	class Test(rank:Int) {
       class R(rank:Int) {
         def check( tt:Test{self.rank==this.rank}) {}       
         def this(r:Int){property(r);}
       }
       def this(r:Int){property(r);}
       var r:R{self.rank == this.rank}=null;
       def m(t:Test{self.rank==this.rank}) {
           r.check(t);
       }
	}

    public def run() = true;
    

    public static def main(Rail[String]) {
	  new CheckThisTypeInCall().execute();
    }

}
