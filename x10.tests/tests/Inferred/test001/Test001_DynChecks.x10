/**
 * Example directly on integers.
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true


import harness.x10Test;
import x10.compiler.InferGuard;

public class Test001_DynChecks extends x10Test {
    static def assert0(assert_arg: Long{ self == 0 }){}

    @InferGuard
    static def f (f_arg: Long) {
	assert0(f_arg);
    }

    public def run(): boolean {
	Test001_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test001_DynChecks().execute();
    }

}
