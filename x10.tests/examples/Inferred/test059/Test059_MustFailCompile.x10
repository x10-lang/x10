/*
 * Variation on Test057
 *
 * Lost of information through type constraint.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test059_MustFailCompile extends x10Test {

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	return true;
    }

    public static def main(Rail[String]) {
    	new Test059_MustFailCompile().execute();
    }

}
