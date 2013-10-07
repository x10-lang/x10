/*
 * recursive function.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test067;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test067_DynChecks_MustFailRun extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: boolean, x: Long) {
	assert0(x);
	if ( b ) { return; }
	f(true, 1);
    }

    public def run(): boolean {
	Test067_DynChecks_MustFailRun.f(false, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test067_DynChecks_MustFailRun().execute();
    }

}
