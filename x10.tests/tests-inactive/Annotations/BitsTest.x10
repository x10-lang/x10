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

//OPTIONS: -PLUGINS=bits.plugin.BitsTypePlugin

import harness.x10Test;
import bits.*;

public class BitsTest extends harness.x10Test {
    var a: int;
    var b: int;
    
    public def run(): boolean = {
    	b = 65536;
    	b = 0;
        a = 7 & 1;
        b = 0x01234567 & 0x1ffff;
        return a == 1 && b == 0x14567;
    }

    public static def main(var args: Rail[String]): void = {
        new BitsTest().execute();
    }
}
