/**
 * Example with useless constraints.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test034 extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(x: Long, y:Long) { /*??<x == 0,>??*/ y == 42} {
        assert0(x);
    }

    public def run(): boolean {
	Test034.f(0, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test034().execute();
    }

}
