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
 * Test that constraints are correctly propagated through when a field's receiver is an expression e.
 * The expression may be of type Foo!. If the field Foo.f is declared of type Fum!, then it must be the
 * case that e.f's home is statically known to be here.

 * @author vj
 * 
 * 	class F(home:Place) {
		val f:F{self.home==this.home};
	    def m(){}
	    def this(f:F{self.home==here}):F { 
	    	property(here); 
	    	this.f=f;
	   }
	}
 */
public class FieldReceiverIsExpr extends x10Test {
    
	class F(heh:Place)  {
		val f:F ;
	    def m(){}
	    def this(f:F ):F { 
	    	property(here); 
	    	this.f=f;
	   }
	}
	def m() { 
	    (new F(null) as F).f.m();
	}

    public def run() = true;

    public static def main(Rail[String]) {
	  new FieldReceiverIsExpr().execute();
    }

}
