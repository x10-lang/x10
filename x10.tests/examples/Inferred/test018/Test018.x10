/**
 * Variation on the example Test15.
 *
 * The contraint is on the method not on the parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test018 extends x10Test {
    static def assert0(x: Long){ x == 0 }{}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z = y;
        val zz = z;
        assert0(zz);
    }

    public def run(): boolean {
	Test018.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test018().execute();
    }

}
