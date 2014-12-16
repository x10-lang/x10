/**
 * Constraint with literal.
 *
 */



//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test044_DynChecks extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}

    @InferGuard
    static def f (x: Long){ /*??< x == 0 >??*/ } {
        val y: Long{self == 0} = 0;
        assertEq(x, y);
    }

    public def run(): boolean {
	Test044_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test044_DynChecks().execute();
    }

}
