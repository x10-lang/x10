/**
 * Variation on the example Test13.
 *
 * Indirection through a function call.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test025 extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    static def id(x: Long): Long{x == self} { return x; }

    @InferGuard
    static def f(y:Long{ /*??< self==0 >??*/}) {
             assert0(id(y));
    }

    public def run(): boolean {
	Test025.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test025().execute();
    }

}
