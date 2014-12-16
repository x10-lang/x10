/**
 * Example with fields and local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test042 extends x10Test {
    static def assertEqPair(x: Pair, y: Pair){x == y} {}

    @InferGuard
    static def f(p:Pair, q:Pair){ /*??< p == q >??*/ } {
        val r = p;
        assertEqPair(q, r);
    }

    public def run(): boolean {
	val p = new Pair(1, 2);
    	Test042.f(p, p);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test042().execute();
    }

}
