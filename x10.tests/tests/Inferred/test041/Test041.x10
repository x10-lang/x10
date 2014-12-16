/**
 * Example with fields and local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test041 extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(p:Pair, q:Pair){ /*??< p.left == q.left >??*/ } {
        val r = p;
        assertEq(q.left, r.left);
    }

    public def run(): boolean {
	Test041.f(new Pair(1,2), new Pair(1, 3));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test041().execute();
    }

}
