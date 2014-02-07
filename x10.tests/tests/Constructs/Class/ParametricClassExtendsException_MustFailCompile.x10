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
 *Check that if the class has a type parameter then it cannot extend Exception.
 *
 * @author vj 11/2013
 */


public class ParametricClassExtendsException_MustFailCompile extends x10Test {
 
    class E extends Exception {}
    class X[T] extends Exception {} // ERR A class with type parameters cannot extend x10.lang.CheckedThrowable.
    class Y[T] extends E {}        // ERR A class with type parameters cannot extend x10.lang.CheckedThrowable.
    public def run()=false;
    public static def main(Rail[String]) {
        new ParametricClassExtendsException_MustFailCompile().execute();
    }
}
