/*
 * Constraint through variable declaration
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test052;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test052 extends x10Test {

    static def assertEq(a: int, b: int){ a == b } {}

    @InferGuard
    static def f (x: int, y: int) {
	val v1: Long{self == 0} = x;
	val v2: Long{self == 0} = y;
	assertEq(x, y);
    }

    public def run(): boolean {
	Test052.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test052().execute();
    }

}
