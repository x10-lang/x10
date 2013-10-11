/**
 * Variation on the example Test13.
 *
 * One level of inderection for the constraint inference.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test014_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(y:Long) {
        val z = y;
        assert0(z);
    }

    public def run(): boolean {
	Test014_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test014_DynChecks().execute();
    }

}
