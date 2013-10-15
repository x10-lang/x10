/**
 * Variation on the example Test15.
 *
 * The contraint is on the method not on the parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test018_DynChecks extends x10Test {
    static def assert0(x: Long){ x == 0 }{}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z = y;
        val zz = z;
        assert0(zz);
    }

    public def run(): boolean {
	Test018_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test018_DynChecks().execute();
    }

}
