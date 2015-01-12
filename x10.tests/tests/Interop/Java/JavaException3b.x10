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

// MANAGED_X10_ONLY

public class JavaException3b extends x10Test {
	static class Sup {
		def this() {}		
	}
	
	static class Sub extends Sup {
		def this() throws x10.lang.CheckedThrowable : Sub {
			throw new x10.lang.CheckedThrowable("I like Java.");          
		}
		def this(a:Int) throws x10.lang.CheckedThrowable : Sub {
			this();
		}
	}
	
    public def run(): Boolean {
    	var pass: Boolean = false;
    	try {
    		new Sub();
    	} catch (e:x10.lang.CheckedThrowable) {
    		pass = true;
    		//e.printStackTrace();
    	}
    	chk(pass);
    	return pass;
    }

    public static def main(args: Rail[String]) {
    	new JavaException3b().execute();
    }
}
