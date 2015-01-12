
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
 * Type parameters may be constrained by a where clause on the
 * declaration (§4.3,§9.5,§9.7,§12.5).
 */

public class ClosureTypeParameters2b_MustFailCompile extends x10Test {

    class V           {public static val name = "V";}
    class W extends V {public static val name = "W";}
    class X extends V {public static val name = "X";}
    class Y extends X {public static val name = "Y";}
    class Z extends X {public static val name = "Z";}

    public def run(): boolean = {
        
        class C[T] {val f = (){T<:Y} => "hi";} // ERR: Type constraints not permitted in closure guards.
        chk(new C[X]().f().equals("hi"));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters2b_MustFailCompile().execute();
    }
}
