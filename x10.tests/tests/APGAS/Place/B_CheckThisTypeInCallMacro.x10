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
 * 
 * Testing that thisType is correctly updated and propagated into the code to check a
 * method call. and that R(this.rank)! is processed correctly.
 * 
 * @author vj
 */

public class B_CheckThisTypeInCallMacro extends x10Test {
    public def run() = true;
    public static def main(Rail[String]) {
	  new 
	  B_CheckThisTypeInCallMacro().execute();
    }

static class R(rank:Int) {
	static type R(r:int)=R{self.rank==r};
	static type Test(r:int)=Test{self.rank==r};
    def check( tt:Test(this.rank)) {}       
    def this(r:Int){property(r);}
  }
static class Test(rank:Int) {
	static type Test(r:int)=Test{self.rank==r};
	static type R(r:int)=R{self.rank==r};
    def this(r:Int){property(r);}
    var r:R(this.rank) =null;
    def m(t:Test(this.rank)) {
         r.check(t);
    }
}

}
