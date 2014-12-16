/**
 * Example with fields and new.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test039 extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(p:Pair){ /*??< p.left == p.right >??*/ } {
        val q = new Pair(p.left, p.right);
        assertEq(q.left, q.right);
    }

    public def run(): boolean {
	Test039.f(new Pair(0, 0));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test039().execute();
    }

}
