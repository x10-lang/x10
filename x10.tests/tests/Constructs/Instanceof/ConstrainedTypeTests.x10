/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

// SKIP_NATIVE_X10: XTENLANG-2388 Runtime evaluation of constraints 
// SKIP_MANAGED_X10: XTENLANG-2388 Runtime evaluation of constraints 

/**
 * Purpose: Check that type tests work
 * @author igorp
 **/
public class ConstrainedTypeTests extends x10Test {

    public def run(): boolean = {
        var res1: boolean = X10DepTypeClassOneB == X10DepTypeClassOneB;
        var res2: boolean = X10DepTypeSubClassOneB <: X10DepTypeClassOneB;
        var res3: boolean = X10DepTypeSubClassOneB <: Int;
        var res4: boolean = X10DepTypeSubClassOneB{a==2} <: X10DepTypeClassOneB{p==2};
        var res5: boolean = X10DepTypeSubClassOneB{a==3} <: X10DepTypeClassOneB{p==2};
        var res6: boolean = X10DepTypeSubClassOneB{p==2} <: X10DepTypeClassOneB{p==2 && p!=3 && p!=4};
        x10.io.Console.OUT.println("X10DepTypeClassOneB == X10DepTypeClassOneB -> "+res1+" (should be true)");
        x10.io.Console.OUT.println("X10DepTypeSubClassOneB <: X10DepTypeClassOneB -> "+res2+" (should be true)");
        x10.io.Console.OUT.println("X10DepTypeSubClassOneB <: Int -> "+res3+" (should be false)");
        x10.io.Console.OUT.println("X10DepTypeSubClassOneB{a==2} <: X10DepTypeClassOneB{p==2} -> "+res4+" (should be false)");
        x10.io.Console.OUT.println("X10DepTypeSubClassOneB{a==3} <: X10DepTypeClassOneB{p==2} -> "+res5+" (should be false)");
        x10.io.Console.OUT.println("X10DepTypeSubClassOneB{p==2} <: X10DepTypeClassOneB{p==2} -> "+res6+" (should be true)");
        
        return (res1 && res2 && !res3 && !res4 && !res5 && res6);
    }

    public static def main(var args: Rail[String]): void = {
        new ConstrainedTypeTests().execute();
    }
}
