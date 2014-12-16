/**
 * Variation on the example Test16.
 *
 * Useless constraint on a local variable.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test045_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z : Long {self == 0} = 0;
        assert0(y);
    }

    public def run(): boolean {
	Test045_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test045_DynChecks().execute();
    }

}
