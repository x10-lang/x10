//OPTIONS: -PLUGINS=dims.plugin.DimensionTypePlugin

import harness.x10Test;
import x10.lang.*;
import dims.*;

public class DimTest2 extends harness.x10Test {
    public boolean run() {
	double @Unit(Temperature.C) meltC = 0;
	double @Unit(Temperature.C) boilC = 100;

	double @Unit(Temperature.F) meltF = 32;
	double @Unit(Temperature.F) boilF = 212;
	
	boolean a = (int) (@Unit(Temperature.F)) meltC == (int) meltF;
	boolean b = (int) (@Unit(Temperature.F)) boilC == (int) boilF;
	boolean c = (int) (@Unit(Temperature.C)) meltF == (int) meltC;
	boolean d = (int) (@Unit(Temperature.C)) boilF == (int) boilC;

	return a && b && c && d;
    }

    public static void main(String[] args) {
        new DimTest2().execute();
    }
}
