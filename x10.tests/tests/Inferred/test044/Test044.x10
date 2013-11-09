/**
 * Constraint with literal.
 *
 */



//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test044 extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}

    @InferGuard
    static def f (x: Long){ /*??< x == 0 >??*/ } {
        val y: Long{self == 0} = 0;
        assertEq(x, y);
    }

    public def run(): boolean {
	Test044.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test044().execute();
    }

}
