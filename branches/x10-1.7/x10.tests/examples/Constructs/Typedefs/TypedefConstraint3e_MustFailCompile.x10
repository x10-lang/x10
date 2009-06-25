// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

class TypedefConstraint3e_MustFailCompile extends TypedefTest {

    public def run():boolean = {

        type T(x:int){x==1} = int;
        val zero = 0;
        var a:T(zero);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint3e_MustFailCompile().execute();
    }
}
