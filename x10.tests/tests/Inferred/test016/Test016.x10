/**
 * Variation on the example Test13.
 *
 * The contraint is on the method not on the parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test016 extends x10Test {
    static def assert0(x: Long){ x == 0 }{}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        assert0(y);
    }

    public def run(): boolean {
	Test016.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test016().execute();
    }

}
