/*
 * recursive function.
 *
 */

//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

//SKIP_MANAGED_X10: XTENLANG-3328 
//SKIP_NATIVE_X10: XTENLANG-3328 


import harness.x10Test;
import x10.compiler.InferGuard;

public class Test067_MustFailCompile extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: boolean, x: Long) {
	assert0(x);
	if ( b ) { return; }
	f(true, 1);
    }

    public def run(): boolean {
	Test067_MustFailCompile.f(false, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test067_MustFailCompile().execute();
    }

}
