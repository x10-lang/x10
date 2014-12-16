/**
 * Simple inference of constraints on method parameters.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test013_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f (y:Long{/*??< self==0 >??*/}) {
             assert0(y);
    }

    public def run(): boolean {
	Test013_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test013_DynChecks().execute();
    }

}
