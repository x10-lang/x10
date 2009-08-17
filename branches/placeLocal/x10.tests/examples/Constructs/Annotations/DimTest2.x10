//OPTIONS: -PLUGINS=dims.plugin.DimensionTypePlugin

import harness.x10Test;
import dims.*;

public class DimTest2 extends harness.x10Test {
    public def run(): boolean = {
	var meltC: double = 0;
	var boilC: double = 100;

	var meltF: double = 32;
	var boilF: double = 212;
	
	var a: boolean = meltC as @Unit(Temperature.F) as int == meltF as int;
	var b: boolean = boilC as @Unit(Temperature.F) as int == boilF as int;
	var c: boolean = meltF as @Unit(Temperature.C) as int == meltC as int;
	var d: boolean = boilF as @Unit(Temperature.C) as int == boilC as int;

	return a && b && c && d;
    }

    public static def main(var args: Rail[String]): void = {
        new DimTest2().execute();
    }
}
