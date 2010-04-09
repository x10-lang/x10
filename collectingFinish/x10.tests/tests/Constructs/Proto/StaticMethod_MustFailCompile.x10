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
 * Cant declare a static method proto.
 * @author vj
 */
public class StaticMethod_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * Cant declare a static method proto.
     */
    static proto def m(a: A) = a;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new StaticMethod_MustFailCompile().execute();
    }
}
