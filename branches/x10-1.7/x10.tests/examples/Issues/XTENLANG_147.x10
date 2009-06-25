// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_147 extends x10Test {

    incomplete def foo1():int;
    incomplete def bar1(()=>int): void;
    
    incomplete def foo2(): void;
    incomplete def bar2(()=>void): void;
    
    public def foo() {
    
        // this is ok
        val f1 = this.foo1.();
        bar1(f1);
        
        // this fails
        val f2 = this.foo2.();
        bar2(f2);
        
        // this is ok
        val f3 = ()=>{};
        bar2(f3);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_147().execute();
    }
}
