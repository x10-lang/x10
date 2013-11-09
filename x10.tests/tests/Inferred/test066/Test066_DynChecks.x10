/*
 * Using contraints inferred on retrun type.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test066_DynChecks extends x10Test {
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
	Test066_DynChecks.g(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test066_DynChecks().execute();
    }

}
