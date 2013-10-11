/**
 * Example with fields
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test037 extends x10Test {
    static def assertEq(x: Long, y: Long){x == y} {}

    @InferGuard
    static def f(x:Long, p:Pair){ /*??< x == p.right >??*/ } {
        assertEq(x, p.right);
    }

    public def run(): boolean {
	Test037.f(2, new Pair(1,2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test037().execute();
    }

}
