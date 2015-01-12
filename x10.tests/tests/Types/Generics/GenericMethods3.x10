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
 * @author bdlucas 8/2008
 */

public class GenericMethods3 extends GenericTest {

    def m[T,U](u:U,t:T) = t;

    public def run() = {

        genericCheck("m[long,String](\"1\",1)", m[long,String]("1",1), 1);
        genericCheck("m[String,long](1,\"1\")", m[String,long](1,"1"), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods3().execute();
    }
}
