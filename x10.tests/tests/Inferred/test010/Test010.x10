/**
 * Variation on the example Test09.
 *
 * The constraint on x depends on y which is defined later.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test010 extends x10Test {
    static def assert_eq(x: Long, y: Long{ self == x}){}

    @InferGuard
    static def f(x: Long, y: Long) {
        assert_eq(x, y);   // <=   x == y
        assert_eq(42, y);  // <=  42 == y
    }

    public def run(): boolean {
	Test010.f(42, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test010().execute();
    }

}
