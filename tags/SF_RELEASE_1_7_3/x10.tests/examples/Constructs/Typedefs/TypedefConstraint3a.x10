// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

class TypedefConstraint3a extends TypedefTest {

    class A(x:int) {def this(x:int):A{self.x==x} = property(x);}

    public def run():boolean = {

        type T(x:int){x==1} = A{self.x==x};
        val one = 1;
        val a:T(one) = new A(one);
        check("a.x", a.x, 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint3a().execute();
    }
}
