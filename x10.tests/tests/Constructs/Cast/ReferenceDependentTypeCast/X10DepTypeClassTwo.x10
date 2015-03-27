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
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassTwo(p: int, q: int) extends x10Test {
    public property p():int = p;
    
    public def this(a: int, b: int): X10DepTypeClassTwo{self.p==a&&self.q==b} {
        property(a,b);
    }
    
    public def run(): boolean {
        var one: X10DepTypeClassTwo{self.p==this.p} = new X10DepTypeClassTwo(this.p,0n);
        return one.p() == 0n;
    }
    
    public static def main(args: Rail[String]): void {
        new X10DepTypeClassTwo(0n,0n).execute();
    }
}
