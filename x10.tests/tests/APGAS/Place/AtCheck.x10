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
 * Testing that an at(b) b.x is legal.
 * @author vj
 */

public class AtCheck extends x10Test {
	private val root = GlobalRef[AtCheck](this);
	var x:AtCheck =null;
    def m(b: GlobalRef[AtCheck]) = at (b) {
	     b().x
    };
    
    public def run()=true;

    public static def main(Rail[String]) {
        new AtCheck().execute();
    }
}
