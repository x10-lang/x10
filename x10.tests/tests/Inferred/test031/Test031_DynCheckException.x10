/**
 * Variation on the example Test29.
 *
 * adding of local variables.
 *
 * Test: Bad
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test031_DynCheckException extends x10Test {
    static def assert_eq (x: Long, y: Long, z: Long){ x == y && y == z } {}

    @InferGuard
    static def f (x1: Long, x2: Long, x3: Long){ /*??< x1 == x2 , x2 == x3, x3 != x1 >??*/ } {
        val v1 = x1;
        val v2 = x2;
        val v3 <: Long{ self != v1 } = x3;
        assert_eq(v1, v2, v3);
    }

    public def mustFailRun(): boolean {
	f(0, 0, 0);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test031_DynCheckException().execute();
    }

}
