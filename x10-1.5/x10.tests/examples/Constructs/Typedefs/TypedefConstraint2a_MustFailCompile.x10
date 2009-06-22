// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

public class TypedefConstraint2a_MustFailCompile extends TypedefTest {

    class X           {const name = "X";}
    class Y extends X {const name = "Y";}
    class Z extends Y {const name = "Z";}

    class FOO[T]{T<:X} {
        //val name = T.name;
    }

    public def run(): boolean = {
        
        type A[T]{T==Y} = FOO[T];
        a1:A[Z] = new FOO[Z]();
        //check("a1.name", a1.name, "Z");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint2a_MustFailCompile().execute();
    }
}
