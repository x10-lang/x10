/*
 * Constraint through variable declaration. Adding of !=
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test053 extends x10Test {

    static def assertDisEq(a: Long, b: Long){ a != b } {}

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == 0} = x;
	val v2: Long{self == 1} = y;
	assertDisEq(x, y);
    }

    public def run(): boolean {
	Test053.f(0, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test053().execute();
    }

}
