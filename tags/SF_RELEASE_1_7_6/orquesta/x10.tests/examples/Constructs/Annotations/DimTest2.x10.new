//OPTIONS: -PLUGINS=dims.plugin.DimensionTypePlugin

import harness.x10Test;
import x10.lang.*;
import dims.*;;

public class DimTest2 extends harness.x10Test {
    public def run(): boolean = {
	var meltC: double = 0;
	var boilC: double = 100;

	var meltF: double = 32;
	var boilF: double = 212;
	
	var a: boolean = (int) (@Unit(Temperature.F)) meltC == (int) meltF;
	var b: boolean = (int) (@Unit(Temperature.F)) boilC == (int) boilF;
	var c: boolean = (int) (@Unit(Temperature.C)) meltF == (int) meltC;
	var d: boolean = (int) (@Unit(Temperature.C)) boilF == (int) boilC;

	return a && b && c && d;
    }

    public static def main(var args: Rail[String]): void = {
        new DimTest2().execute();
    }
}
