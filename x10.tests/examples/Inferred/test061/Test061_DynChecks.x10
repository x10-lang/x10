/*
 * Multiple functions
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test061_DynChecks extends x10Test {

    @InferGuard
    static def f(x: Long) {
	val v: Long{self == 0} = x;
    }

    @InferGuard
    static def g(x: Long) {
	f(x);
    }

    public def run(): boolean {
	Test061_DynChecks.g(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test061_DynChecks().execute();
    }

}
