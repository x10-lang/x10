/**
 * Example with fields
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test036_DynChecks extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(p:Pair){ /*??< p.left == p.right >??*/ } {
        assertEq(p.left, p.right);
    }

    public def run(): boolean {
	Test036_DynChecks.f(new Pair(3,3));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test036_DynChecks().execute();
    }

}
