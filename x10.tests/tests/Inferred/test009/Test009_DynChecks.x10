/**
 * Example directly on integers.
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true


import harness.x10Test;
import x10.compiler.InferGuard;

public class Test009_DynChecks extends x10Test {
    static def assert_eq(x: Long, y: Long{ self == x}){}

    @InferGuard
    static def f(x:Long) {
        assert_eq(42, x);  // <=  42 == x
    }

    public def run(): boolean {
	Test009_DynChecks.f(42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test009_DynChecks().execute();
    }
}
