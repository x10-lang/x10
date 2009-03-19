// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

class TypedefConstraint3d_MustFailCompile extends TypedefTest {

    public def run():boolean = {

        type T(x:int){x==1} = int;
        val one:int = 1;
        var a:T(one);

    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint3d_MustFailCompile().execute();
    }
}
