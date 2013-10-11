/**
 * Local variables.
 *
 * Test: Bad
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test040_MustFailCompile extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f() {
        var x:Long;
        x = 42;
        assert0(x);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
    	new Test040_MustFailCompile().execute();
    }

}
