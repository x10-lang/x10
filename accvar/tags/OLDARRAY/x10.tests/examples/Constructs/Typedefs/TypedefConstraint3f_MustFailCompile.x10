// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

class TypedefConstraint3f_MustFailCompile extends TypedefTest {

    public def run():boolean = {

        type T(x:int,y:int){x==1&&y==-1} = int;
        var a:T(1,1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint3f_MustFailCompile().execute();
    }
}
