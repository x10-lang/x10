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
 * @author vj 03/2009
 */

class XTENLANG_345 extends x10Test {

    interface Set[T] {
	def add(x:Set[T]):Set[T];
    }
    
    public def run()=true;
    public  static def all[T](x:Rail[T]):Set[Any] { throw new Exception(); }
    public static def main(Rail[String]) {
        new XTENLANG_345().execute();
    }
}
