// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

public class TypedefWhere1c_MustFailCompile extends TypedefTest {

    class X           {def name() = "X";}
    class Y extends X {def name() = "Y";}
    class Z extends Y {def name() = "Z";}

    public def run(): boolean = {
        
        type C[T]{Y<:T} = T;
        c1:C[Z] = new Z();
        check("c1.name()", c1.name(), "Z");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefWhere1c_MustFailCompile().execute();
    }
}
