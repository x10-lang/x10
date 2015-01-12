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

//OPTIONS: -PLUGINS=dims.plugin.DimensionsTypePlugin

import harness.x10Test;
import x10.lang.annotations.*;
import dims.*;

public class DimTest extends harness.x10Test {
    def move(var x0: double, var v0: double, var a: double, var t: double) =
      x0 + (v0 * t) + (a * t * t) / 2;
    public def run(): boolean = {
        var x0: double = 0;
        var t: double = 1;
        var v0: double = 0;
        var a: double = Acceleration.g;

        var x1: double = move(x0, v0, a, t);

        return x1 == 4.9;
    }

    public static def main(var args: Rail[String]): void = {
        new DimTest().execute();
    }
}
