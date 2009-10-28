// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_215 extends x10Test {

    static class Foo {
    
        val foo = 0;
        
        class Bar {
            val bar = foo;
        }
        
        def bar() = new Bar();
    }
    
    public def run():boolean {
        val foo = new Foo();
        val bar = foo.bar();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_215().execute();
    }
}
