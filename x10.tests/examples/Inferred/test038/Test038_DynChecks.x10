/**
 * Example with fields and new.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test038_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(e:Elt){ /*??< e.elt == 0 >??*/ } {
        val q = new Elt(e.elt);
        assert0(e.elt);
    }

    public def run(): boolean {
	Test038_DynChecks.f(new Elt(0));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test038_DynChecks().execute();
    }

}
