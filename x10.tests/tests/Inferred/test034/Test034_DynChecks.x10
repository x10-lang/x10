/**
 * Example with useless constraints.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test034_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(x: Long, y:Long) { /*??<x == 0,>??*/ y == 42} {
        assert0(x);
    }

    public def run(): boolean {
	Test034_DynChecks.f(0, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test034_DynChecks().execute();
    }

}
