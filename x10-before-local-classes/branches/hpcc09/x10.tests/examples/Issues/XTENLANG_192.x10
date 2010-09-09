// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_192 extends x10Test {

    static class C[T](x:int) {
        static def make[T](x:int):C[T]{self.x==x} = new C[T](x);
        def this(x:int):C[T]{self.x==x} = property(x);
    }
    
    val c: C[int] = C.make[int](0);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_192().execute();
    }
}
