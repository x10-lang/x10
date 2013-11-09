/*
 * Constraint through variable declaration. Adding of !=
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test054_DynChecks extends x10Test {

    static def assertDisEq(a: Long, b: Long){ a != b } {}

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == 0} = x;
	val v2: Long{self != 0} = y;
	assertDisEq(x, y);
    }

    public def run(): boolean {
	Test054_DynChecks.f(0, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test054_DynChecks().execute();
    }

}
