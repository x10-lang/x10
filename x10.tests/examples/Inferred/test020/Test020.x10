/**
 * Variation on the example Test13.
 *
 * z is not directly an alias of the parameter. We do not infer constraint, here!
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test020 extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: Boolean, y1: Long, y2:Long){ y1==0 , y2==0 } {
        val z = b ? y1 : y2;
        assert0(z);
    }

    public def run(): boolean {
	Test020.f(true, 0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test020().execute();
    }

}
