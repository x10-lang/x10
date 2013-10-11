/**
 * Test with boolean values.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test027_DynChecks extends x10Test {
    static def assert_true (x:Boolean{ self == true }) {}

    @InferGuard
    static def f (x: Boolean) { /*??< x == true >??*/ } {
        assert_true(x);
    }

    public def run(): boolean {
	Test027_DynChecks.f(true);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test027_DynChecks().execute();
    }

}
