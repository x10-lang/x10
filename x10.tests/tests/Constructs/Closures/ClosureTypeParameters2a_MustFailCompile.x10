
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

public class ClosureTypeParameters2a_MustFailCompile extends x10Test {

    class V           {static name = "V";}
    class W extends V {static name = "W";}
    class X extends V {static name = "X";}
    class Y extends X {static name = "Y";}
    class Z extends X {static name = "Z";}

    public def run(): boolean = {
        
        class C[T]{T==Y} {val f = ()=> "hi";}
        chk(new C[Z] // ERR ERR: Type C[ClosureTypeParameters2a_MustFailCompile.Z] is inconsistent.   Actual type of property initializer is not a subtype of declared type.
            ().f().equals("hi"));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters2a_MustFailCompile().execute();
    }
}
