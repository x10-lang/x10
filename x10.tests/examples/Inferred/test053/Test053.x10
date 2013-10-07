/*
 * Constraint through variable declaration. Adding of !=
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test053;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test053 extends x10Test {

    static def assertDisEq(a: int, b: int){ a != b } {}

    @InferGuard
    static def f (x: int, y: int) {
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
