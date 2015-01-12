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
 * A struct constructor can be invoked with new.
 * @author vj
 */
public class NewForStruct extends x10Test {

    static struct Metajamjirrox {
        val a : Int;
        def this(b : Int) { a = b; }
    }

    public def run() {
        val m : Metajamjirrox  = new Metajamjirrox(1n);
    	return true;
    }

    public static def main(Rail[String])  {
	new NewForStruct().execute();
    }
}
