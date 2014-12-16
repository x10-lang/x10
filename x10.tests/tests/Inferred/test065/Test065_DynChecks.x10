/*
 * Using contraints inferred on retrun type.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test065_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(x: Long) {
	val v: Long{self == 0} = x;
	return v;
    }

    static def g() {
	assert0(f(0));
    }

    public def run(): boolean {
	Test065_DynChecks.g();
        return true;
    }

    public static def main(Rail[String]) {
    	new Test065_DynChecks().execute();
    }

}
