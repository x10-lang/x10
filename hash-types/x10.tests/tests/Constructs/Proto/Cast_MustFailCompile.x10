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
 * Casts to proto types are not permitted.
 * @author vj
 */
public class Cast_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * Casts to proto types are not permitted.
     */
    def m(a: A): proto A = a as proto A;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new Cast_MustFailCompile().execute();
    }
}
