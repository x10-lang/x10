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
 * Closure expressions have zero or more type parameters,
 * 
 * Methods, constructors, closures, and type definitions may have type
 * parameters, which are instantiated with concrete types at invocation
 * (§4.2).
 * 
 * A method, constructor, or closure may have type parameters whose scope
 * is the signature and body of the declaring method, constructor or
 * closure.
 * 
 *
 * @author bdlucas 8/2008
 */

public class ClosureTypeParameters1g extends x10Test {

    public def run(): boolean = {
        
        class X[T,U] {val f = (x:T,y:U) => x.toString() + y.toString();}
        chk(new X[String,String]().f("1","1").equals("11"));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters1g().execute();
    }
}
