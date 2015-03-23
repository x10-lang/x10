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
 *Check that it is possible to define and create instances of classes inside a method.
 *
 * @author vj 3/2010
 */


public class InnerClass extends x10Test {

    public def run(): boolean {
        
    		class X {}
    		class Y {}
    		val result = new X().equals(new Y());
    		
        return ! result;
    }

    public static def main(Rail[String]) {
        new InnerClass().execute();
    }
}
