/**
 * Example with fields
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test036 extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(p:Pair){ /*??< p.left == p.right >??*/ } {
        assertEq(p.left, p.right);
    }

    public def run(): boolean {
	Test036.f(new Pair(3,3));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test036().execute();
    }

}
