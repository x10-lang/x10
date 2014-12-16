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
 * Changed for 2.1.
 * 
 * Test that if you have two fields with GlobalRef's pointing to the same location, you can do an at to
 * one field and deref the other.
 * 
 * Same as AtThisIntohere.x10, but without Static calls.

 * @author vj
 */
public class B_AtThisIntoAtHere extends x10Test {
    class Test {

     def n() { 
    	val x  = GlobalRef[Test](this);
        val y:GlobalRef[Test]{self.home==x.home} = GlobalRef[Test](this);
    	 
    	 at (x) {
    		 // it is ok to invoke this.y() at the place of this.x.
    		 y();
    	 }
	     
     }
    }

    public def run() = true;

    public static def main(Rail[String]) {
	  new B_AtThisIntoAtHere().execute();
    }

}
