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
 * Testing that a value created remotely has the right type so it can be accessed safely within a subsequent remote at.
 */
public class AtFieldWrite extends x10Test {
	var t: GlobalRef[T] = GlobalRef[T](null);
    public def run() {
       val second = Place.places().next(here);
       val newT = (at (second) new T()).root;
       at (newT) { 
	      newT().i = 3n; 
       }
       return true;
    }

    public static def main(Rail[String]) {
	   new AtFieldWrite().execute();
    }

    static class T {
       private val root = GlobalRef[T](this);
	   transient public var i: int;
    }
}
