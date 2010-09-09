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

//OPTIONS: -STATIC_CALLS

import harness.x10Test;

/**
 * 
 * Testing that a this field marked at(this) can be sent into a method requiring an at here, provided that
 * there has been no place shift between invocation of the method and the method call.

 * @author vj
 */
public class AtThisIntoAtHere1_MustFailCompile extends x10Test {
    class Test {
      var x:Test{self.at(this)}=null;
    
     def m(b:Test{self.at(here)}) {}
     def n() { 
	 // n() is a method not marked global, hence on method entry one can assume that this.home==here.
	 // therefore this.x also satisfies at(here) (since its type satisfies at(this)). 
	 // Hence this is a legal call.
    	 val p = Place.places(1);
    	 this.m(at(p) (this.x));
    }
    }

    public def run() = true;

    public static def main(Rail[String]) {
	  new AtThisIntoAtHere1_MustFailCompile().execute();
    }

}
