/**
 * Example with fields
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test035_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(p:Pair){ /*??< p.left == 0 >??*/ } {
        assert0(p.left);
    }

    public def run(): boolean {
	Test035_DynChecks.f(new Pair(0, 1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test035_DynChecks().execute();
    }

}
