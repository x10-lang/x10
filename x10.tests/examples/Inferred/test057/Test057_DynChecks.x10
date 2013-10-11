/*
 * Constraint through variable declaration.
 * Equality constraint using local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test057_DynChecks extends x10Test {

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == x} = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	Test057_DynChecks.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test057_DynChecks().execute();
    }

}
