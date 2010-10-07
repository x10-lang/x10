// Yoav added: IGNORE_FILE
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

import harness.x10Test;



/**
 * Check that it is ok to initialize a field with an instance of an instance inner class
 * Yoav: it's not ok under the new initialization rules. "this" cannot escape.
 This is equivalent to the prohibited pattern:
    static class A(outer:InitFieldWithInstanceClass) {}
    val a = new A(this); // "this" cannot escape!
We can't allow either pattern.
 */

public class InitFieldWithInstanceClass extends x10Test {

    class A { 
    }
    val a = new A(); // if you are going to do this, then you have to mark A as static.
  
    public def run() =true;

    public static def main(Array[String](1)) {
        new InitFieldWithInstanceClass().execute();
    }
}
