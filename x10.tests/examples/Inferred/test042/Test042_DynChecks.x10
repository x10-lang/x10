/**
 * Example with fields and local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test042_DynChecks extends x10Test {
    static def assertEqPair(x: Pair, y: Pair){x == y} {}

    @InferGuard
    static def f(p:Pair, q:Pair){ /*??< p == q >??*/ } {
        val r = p;
        assertEqPair(q, r);
    }

    public def run(): boolean {
	val p = new Pair(1, 2);
    	Test042_DynChecks.f(p, p);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test042_DynChecks().execute();
    }

}
