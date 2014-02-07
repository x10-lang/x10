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
  * Cannot have a nested &&.
 */

public class IllegalConstraint3_MustFailCompile  extends x10Test {
    class D(c1:Boolean, c2:Boolean){}
    static type D(b:boolean) = D{b==(self.c1&&self.c2)}; //ERR
	
    public def run() = true;

    public static def main(Rail[String])  {
        new IllegalConstraint3_MustFailCompile().execute();
    }
}
