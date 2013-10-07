/**
 * Local variables.
 *
 * Test: Bad
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test040;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test040_DynChecks_MustFailRun extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f() {
        var x:Long;
        x = 42;
        assert0(x);
    }

    public def run(): boolean {
	f();
        return true;
    }

    public static def main(Rail[String]) {
    	new Test040_DynChecks_MustFailRun().execute();
    }

}
