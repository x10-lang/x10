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
