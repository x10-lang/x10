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
 * A field cannot have a proto type.
 * @author vj
 */
public class ProtoField_MustFailCompile extends x10Test {

	class A{}
   var x: proto A;
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoField_MustFailCompile().execute();
    }
}
