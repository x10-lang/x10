//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test056;

import harness.x10Test;

public class Test056_MustFailCompile extends x10Test {

    static def assertEq(a: int, b: int){ a == b } {}
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f (x: int, y: int) {
	val v1: Long{self == 0} = x;
	val v2: Long{self != 0} = y;
	assertEq(x, y);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
    	new Test056_MustFailCompile().execute();
    }

}
