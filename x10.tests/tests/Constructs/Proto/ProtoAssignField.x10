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
 * It is ok to assign a field of a proto receiver.
 * @author vj
 */
public class ProtoAssignField extends x10Test {

    class A {
    	var x: int;
    }
    
    
    /**
     * Cannot read a field of a proto value.
     */
    def m(a: proto A) {
    	a.x = 5;
    }
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoAssignField().execute();
    }
}
