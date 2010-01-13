// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_48 extends x10Test {

    static class R(rank:int) {
        def this() { property(0); }
    }
    
    static class C[T] {
        incomplete static def make[T](): C[T];
    }
    
    class Bug(foo:int) {
        def this() { property(0); }

        //var a: Rail[R{rank==foo}] = Rail.make[R{rank==foo}](10);
        var a: C[R{rank==foo}] = C.make[R{rank==foo}]();
    }
    
    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_48().execute();
    }
}
