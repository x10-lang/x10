/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



/**
 * @author makinoy 4/2010
 */

public class GenericInstanceof15 extends GenericTest {

    interface I[T1,T2] {}

    class A implements I[Long,Double] {}
    
    public def run() = {
        return new A() instanceof I[Long, Double];
    }
    
    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof15().execute();
    }
}
