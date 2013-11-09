/*
 * Fields. Variation of Test068.
 *
 * With local variables
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test071_DynChecks extends x10Test {
    static def assertEq (x: Long, y: Long){ x == y } {}
    static def assertEqB (x: B, y: B){ x == y } {}

    @InferGuard
    static def f (a: A, b: B) {
	val aa = a.a;
	val aab = aa.b;
	val bb = b.b;
	assertEq(aab, bb);
	val a2 = a.a;
	val bAux = b;
	assertEqB(a2, bAux);
    }

    public def run(): boolean {
	val b = new B(0);
	val a = new A(b);
	Test071_DynChecks.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test071_DynChecks().execute();
    }

}
