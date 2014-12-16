/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_147 extends x10Test {

    def foo1():int { throw new Exception(); }
    def bar1(()=>int): void { throw new Exception(); }
    
    def foo2(): void { throw new Exception(); }
    def bar2(()=>void): void { throw new Exception(); }
    
    public def foo() {
    
        // this is ok
        val f1 = ()=>this.foo1();
        bar1(f1);
        
        // this fails
        val f2 = ()=>{ this.foo2(); };
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
