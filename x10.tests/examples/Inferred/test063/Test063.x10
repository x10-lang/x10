/*
 * Variation on Test061.
 *
 * With return type
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test063 extends x10Test {

    @InferGuard
    static def f(x: Long): Long {
	val v: Long{self == 0} = x;
	return v;
    }

    @InferGuard
    static def g(x: Long): Long {
	return f(x);
    }

    public def run(): boolean {
	Test063.g(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test063().execute();
    }

}
