/*
 * Variation on Test057
 *
 * Infer type of v1 but with subtyping information.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test060 extends x10Test {

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1 <: Long = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	Test060.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test060().execute();
    }

}
