/*
 * Constraint through variable declaration.
 * Equality constraint using local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test057 extends x10Test {

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == x} = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	Test057.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test057().execute();
    }

}
