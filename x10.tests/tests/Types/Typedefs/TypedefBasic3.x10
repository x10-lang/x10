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
 * Basic typdefs and type equivalence.
 *
 * Type definitions are applicative, not generative; that is, they define
 * aliases for types and do not introduce new types.
 *
 * @author bdlucas 9/2008
 */

public class TypedefBasic3 extends TypedefTest {

    public def run(): boolean = {
        
        type T1 = ()=>String;
        type T2 = ()=>String;

        var t0:()=>String = ()=>"0";
        var t1:T1 = ()=>"1";
        var t2:T2 = ()=>"2";

        t0 = t1;
        t1 = t2;
        t2 = t0;

        check("t0()", t0(), "1");
        check("t1()", t1(), "2");
        check("t2()", t2(), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefBasic3().execute();
    }
}
