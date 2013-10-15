/*
 * Fields. Variation of Test068.
 *
 * With local variables. change parameter order.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test072_DynChecks extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}
    static def assertEqB (x: B, y: B){ x == y } {}

    @InferGuard
    static def f (a: A, b: B) {
	val aa = a.a;
	val aab = aa.b;
	val bb = b.b;
	assertEq(bb, aab);
	val a2 = a.a;
	val bAux = b;
	assertEqB(a2, bAux);
    }

    public def run(): boolean {
	val b = new B(0);
	val a = new A(b);
	Test072_DynChecks.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test072_DynChecks().execute();
    }

}
