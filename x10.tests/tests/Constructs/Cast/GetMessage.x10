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
 * Check that the error message from a class cast is correct
 */
public class GetMessage extends x10Test {
	public def run(): boolean = {
        val s = new Empty();
        try {
            val tmp = s as Any as Comparable[Int]; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
            Console.ERR.println("Cast should have failed.");
            return false;
        } catch (e:ClassCastException) {
            if (e.getMessage()==null || !e.getMessage().equals("x10.lang.Comparable[x10.lang.Int]")) {
                Console.ERR.println("Throwable cast had wrong message: "+e.getMessage());
                return false;
            }
        }
        try {
            val tmp = s as Any as Int;
            Console.ERR.println("Cast should have failed.");
            return false;
        } catch (e:ClassCastException) {
            if (e.getMessage()==null || !e.getMessage().equals("x10.lang.Int")) {
                Console.ERR.println("Int cast had wrong message: "+e.getMessage());
                return false;
            }
        }
        return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GetMessage().execute();
	}


}
