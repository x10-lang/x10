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
 * @author bdlucas 8/2008
 */

public class GenericMethods4_MustFailCompile extends GenericTest {

    def m[T](T) = {};

    public def run() = {

        m[long]("1"); // ERR: Method m[T](id$45: T): void in GenericMethods4_MustFailCompile{self==GenericMethods4_MustFailCompile#this} cannot be called with arguments [x10.lang.Long](x10.lang.String{self=="1"});    Invalid Parameter.

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods4_MustFailCompile().execute();
    }
}
