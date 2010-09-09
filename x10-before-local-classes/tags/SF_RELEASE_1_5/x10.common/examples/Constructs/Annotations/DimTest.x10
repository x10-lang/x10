//OPTIONS: -PLUGINS=dims.plugin.DimensionsTypePlugin

import harness.x10Test;
import x10.lang.*;
import x10.lang.annotations.*;
import dims.*;

public class DimTest extends harness.x10Test {
    double @Unit(Length.m)
        move(double @Unit(Length.m) x0,
             double @Unit(Velocity.mps) v0,
             double @Unit(Length.m / (Time.s*Time.s)) a,
             double @Unit(Time.s) t) {

        return x0 + (v0 * t) + (a * t * t) / 2;
    }
    
    public boolean run() {
        double @Unit(Length.m) x0 = 0;
        double @Unit(Time.s) t = 1;
        double @Unit(Length.m / Time.s) v0 = 0;
        double @Unit(Length.m / (Time.s * Time.s)) a = Acceleration.g;

        double @Unit(Length.m) x1 = move(x0, v0, a, t);

        return x1 == 4.9;
    }

    public static void main(String[] args) {
        new DimTest().execute();
    }
}
