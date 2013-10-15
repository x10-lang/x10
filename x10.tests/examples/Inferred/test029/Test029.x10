/**
 * Constraint with a &&.
 *
 */



//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test029 extends x10Test {
    static def assert_eq (x: Long, y: Long, z: Long){ x == y && y == z } {}

    @InferGuard
    static def f (x1: Long, x2: Long, x3: Long){ /*??< x1 == x2 , x2 == x3 >??*/ } {
        assert_eq(x1, x2, x3);
    }

    public def run(): boolean {
	Test029.f(1, 1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test029().execute();
    }

}
