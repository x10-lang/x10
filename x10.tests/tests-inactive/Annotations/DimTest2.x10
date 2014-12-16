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

//LIMITATION: no plugins in dims
//OPTIONS: -PLUGINS=dims.plugin.DimensionTypePlugin

import harness.x10Test;
import dims.*;

public class DimTest2 extends harness.x10Test {
    public def run(): boolean = {
	var meltC: double = 0;
	var boilC: double = 100;

	var meltF: double = 32;
	var boilF: double = 212;
	
	var a: boolean = ((meltC as double@Unit(Temperature.F)) as int) == (meltF as int); // XTENLANG-1124
	var b: boolean = ((boilC as double@Unit(Temperature.F)) as int) == (boilF as int); // XTENLANG-1124
	var c: boolean = ((meltF as double@Unit(Temperature.C)) as int) == (meltC as int); // XTENLANG-1124
	var d: boolean = ((boilF as double@Unit(Temperature.C)) as int) == (boilC as int); // XTENLANG-1124

	return a && b && c && d;
    }

    public static def main(Rail[String]) {
        new DimTest2().execute();
    }
}
