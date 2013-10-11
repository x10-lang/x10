/*
 * Variation on Test062.
 *
 * With return type
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test064 extends x10Test {

    @InferGuard
    static def g(x: Long): Long {
	return f(x);
    }

    @InferGuard
    static def f(x: Long): Long {
	val v: Long{self == 0} = x;
	return v;
    }

    public def run(): boolean {
	Test064.g(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test064().execute();
    }

}
