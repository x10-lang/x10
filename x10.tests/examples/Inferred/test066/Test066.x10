/*
 * Mutually recursive functions.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test066 extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: boolean, x: Long, y: Long) {
	assert0(x);
	if ( b ) { return; }
	g(y, x);
    }

    @InferGuard
    static def g(x: Long, y: Long) {
	assert0(x);
	f(true, y, x);
    }

    public def run(): boolean {
	Test066.g(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test066().execute();
    }

}
