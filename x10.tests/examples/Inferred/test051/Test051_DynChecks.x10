/*
 * Constraint through variable declaration
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test051_DynChecks extends x10Test {

    @InferGuard
    public static def f (y: Long) {
	val v: Long{self == 0} = y;
    }

    public def run(): boolean {
	Test051_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test051_DynChecks().execute();
    }

}
