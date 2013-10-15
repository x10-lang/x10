/*
 * Constraint through variable declaration. Adding of !=.
 * Check that we do not forget hypothesis.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test055_DynChecks extends x10Test {

    static def assertDisEq(a: Long, b: Long){ a != b } {}
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == 0} = x;
	val v2: Long{self != 0} = y;
	assertDisEq(x, y);
	assert0(x);
    }

    public def run(): boolean {
	Test055_DynChecks.f(0, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test055_DynChecks().execute();
    }

}
