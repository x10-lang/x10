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
 * A proto method must be called on a proto receiver.
 * @author vj
 */
public class ProtoCall_MustFailCompile extends x10Test {

    class A {}
    val a:A;
    
    /**
     * This should be declared proto since it is called on this from within a receiver.
     */
    def makeA():A = new A();
    
    public def this() {
    	a = makeA();
    }
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoCall_MustFailCompile().execute();
    }
}
