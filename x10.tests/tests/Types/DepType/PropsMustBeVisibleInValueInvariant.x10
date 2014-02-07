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
 * Checks that properties i and j can be used in the invariant for the class for a struct
 *
 * @author  vj
 */
public class PropsMustBeVisibleInValueInvariant extends x10Test {

    static struct Value2(i:int, j:int){i==j}  {
        public def this(k:int):Value2{self.i==k} = {
            property(k,k);
        }
    }
    public def run() = {
        Value2(4n);
        return true;
    }
    public static def main(Rail[String]): void = {
        new PropsMustBeVisibleInValueInvariant().execute();
    }
}
