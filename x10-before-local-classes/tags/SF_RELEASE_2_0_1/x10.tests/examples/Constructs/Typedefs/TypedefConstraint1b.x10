// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

public class TypedefConstraint1b extends TypedefTest {

    class X           {def name(): String = "X";}
    class Y extends X {def name(): String = "Y";}
    class Z extends Y {def name(): String = "Z";}

    public def run(): boolean = {
        
        type B[T]{T<:Y} = T;
        b:B[Y]! = new Y();
        check("b.name()", b.name(), "Y");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint1b().execute();
    }
}
