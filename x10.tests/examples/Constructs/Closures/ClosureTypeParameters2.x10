// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Type parameters may be constrained by a where clause on the
 * declaration (§4.3,§9.5,§9.7,§12.5).
 */

public class ClosureTypeParameters2 extends ClosureTest {

    class X           {static val name = "X";}
    class Y extends X {static val name = "Y";}
    class Z extends Y {static val name = "Z";}

    public def run(): boolean = {
        

        val a = [T](){T==Y} => T.name;
Y yes
Z no

        val b = [T](){T<:Y} => T.name;
Y yes
X no

        val c = [T](){Y<:T} => T.name;
X yes
Z no
        


        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters2().execute();
    }
}
