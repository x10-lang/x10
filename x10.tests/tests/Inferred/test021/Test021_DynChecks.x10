/**
 * Variation on the example Test20.
 *
 * The value of y1 and y2 depends on a parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test021_DynChecks extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: Boolean, dummy: Long, y1: Long{self == dummy}, y2:Long{ self == dummy }){ /*??< dummy==0 >??*/} {
        val z = b ? y1 : y2;
        assert0(z);
    }

    public def run(): boolean {
	Test021_DynChecks.f(true, 0, 0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test021_DynChecks().execute();
    }

}
