
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
 * Check that it is illegal to initialize a field with an instance of an instance inner class.
 * "this" escape as the receiver of "this. new InnerClass() "
 */

public class InitFieldWithInstanceClass_MustFailCompile extends x10Test {

    class A { 
    }
    val a = new A(); // ERR
  
    public def run() =true;

    public static def main(Rail[String]) {
        new InitFieldWithInstanceClass_MustFailCompile().execute();
    }
}
