/**
 * Example directly on integers.
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true


import harness.x10Test;
import x10.compiler.InferGuard;

public class Test009 extends x10Test {
    static def assert_eq(x: Long, y: Long{ self == x}){}

    @InferGuard
    static def f(x:Long) {
        assert_eq(42, x);  // <=  42 == x
    }

    public def run(): boolean {
	Test009.f(42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test009().execute();
    }
}
