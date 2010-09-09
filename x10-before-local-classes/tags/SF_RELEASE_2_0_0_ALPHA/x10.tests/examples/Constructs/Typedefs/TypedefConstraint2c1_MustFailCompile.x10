// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

public class TypedefConstraint2c1_MustFailCompile extends TypedefTest {

    class X           {const name = "X";}
    class Y extends X {const name = "Y";}
    class Z extends Y {const name = "Z";}

    class FOO[T]{T<:X} {
        //val name = T.name;
    }

    public def run(): boolean = {
        
        type C[T]{T<:Y} = FOO[T];
        c:C[X] = new FOO[X](); // X<:Y make C[X] inconsistent
        //check("c.name", c.name, "X");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint2c1_MustFailCompile().execute();
    }
}
