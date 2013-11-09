/*
 * Fields. Variation of Test068.
 *
 * Only assertEq.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test070 extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}
    static def assertEqB (x: B, y: B){ x == y } {}

    @InferGuard
    static def f (a: A, b: B) {
	assertEq(a.a.b, b.b);
    }

    public def run(): boolean {
	val b = new B(0);
	val a = new A(b);
	Test070.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test070().execute();
    }

}
