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

public class ClosureTypeParameters1c extends ClosureTest {
/*
The following block of class definitions messed up compilation, but were not
otherwise being used. I have commented them out.
    class V           {public static val name = "V";}
    class W extends V {public static val name = "W";}
    class X extends V {public static val name = "X";}
    class Y extends X {public static val name = "Y";}
    class Z extends X {public static val name = "Z";}
*/

    public def run(): boolean = {
        
        class X[T] {val f = (x:T,y:T) => x.toString() + y.toString();}
        check("new X[String]().f(\"1\",\"1\")", (new X[String]().f)("1","ow"), "1ow");

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new ClosureTypeParameters1c().execute();
    }
}
