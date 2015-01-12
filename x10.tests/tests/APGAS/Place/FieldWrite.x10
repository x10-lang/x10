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
 * Testing that fields can be assigned within the body of an instance method if there
 * is no intervening at to place-shift.
 * @author vj
 */
public class FieldWrite extends x10Test {
	var t: T;
    public def run() {
	  this.t = new T(); 
      return true;
    }

    public static def main(Rail[String]) {
	  new FieldWrite().execute();
    }

    static class T {
	  public var i: int;
    }
}
