/**
 * Example from Vijay.
 *
 */

//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test004 extends x10Test {

    public def run(): boolean {
	val v = new Vec(42);
	Vec.cp(v, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test004().execute();
    }

}
