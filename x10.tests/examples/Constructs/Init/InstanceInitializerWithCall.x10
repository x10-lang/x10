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
 * Check that it is ok to have an instance initializer invoke a private or final method. Same restrictions
 * on escaping this apply to this method as if it were called from a constructor.
 */

public class InstanceInitializerWithCall  extends x10Test {

    final def m()=1;
    { 
    	m();
    }
  
    public def run() =true;

    public static def main(Array[String](1)) {
        new InstanceInitializerWithCall().execute();
    }
}
