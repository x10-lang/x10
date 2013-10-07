/*
 * Variation on Test057
 *
 * Infer type of v1 but with subtyping information.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test060;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test060_DynChecks extends x10Test {

    @InferGuard
    static def f (x: int, y: int) {
	val v1 <: Long = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	Test060_DynChecks.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test060_DynChecks().execute();
    }

}
