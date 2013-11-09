/*
 * Constraint through variable declaration
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test051 extends x10Test {

    @InferGuard
    public static def f (y: Long) {
	val v: Long{self == 0} = y;
    }

    public def run(): boolean {
	Test051.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test051().execute();
    }

}
