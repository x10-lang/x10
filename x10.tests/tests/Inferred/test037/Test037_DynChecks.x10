/**
 * Example with fields
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test037_DynChecks extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(x:Long, p:Pair){ /*??< x == p.right >??*/ } {
        assertEq(x, p.right);
    }

    public def run(): boolean {
	Test037_DynChecks.f(2, new Pair(1,2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test037_DynChecks().execute();
    }

}
