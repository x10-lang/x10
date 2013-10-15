/**
 * Variation on the example Test14.
 *
 * The contraint is on the method not on the parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test017_DynChecks extends x10Test {
    static def assert0(x: Long){ x == 0 }{}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z = y;
        assert0(z);
    }

    public def run(): boolean {
	Test017_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test017_DynChecks().execute();
    }

}
