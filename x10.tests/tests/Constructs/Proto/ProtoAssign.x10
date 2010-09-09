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
 * Checks that it is ok to assign a proto A to a field of type A, 
 * where the receiver is proto.
 * @author vj
 */
public class ProtoAssign extends x10Test {

    class A {
    	val x:A;
    	def this() { x=null;}
    	def this(x: proto A):proto A {
    		this.x=x;
    	}
    }
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoAssign().execute();
    }
}
