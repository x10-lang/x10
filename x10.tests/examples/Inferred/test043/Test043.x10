/**
 * Constraint with literal.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test043 extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}

    @InferGuard
    static def f (x: Long){ /*??< x == 0 >??*/ } {
        assertEq(x, 0);
    }

    public def run(): boolean {
	Test043.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test043().execute();
    }

}
