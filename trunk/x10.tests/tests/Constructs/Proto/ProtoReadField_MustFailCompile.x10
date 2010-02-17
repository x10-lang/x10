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
 * Cannot read a field of a proto value.
 * @author vj
 */
public class ProtoReadField_MustFailCompile extends x10Test {

    class A {
    	var x: int;
    }
    
    
    /**
     * Cannot read a field of a proto value.
     */
    def m(a: proto A):int = a.x;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoReadField_MustFailCompile().execute();
    }
}
