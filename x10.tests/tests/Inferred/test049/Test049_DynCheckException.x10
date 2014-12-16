/**
 * inconsistant constraints
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test049_DynCheckException extends x10Test {
    static def assert0(x: Long{ self == 0 }){}
    static def assert1(x: Long{ self == 1 }){}
    static def assertEq(a: Long, b: Long){ a == b } {}

    def f(y1:Long, y2: Long) { /*y1 == 0, y2 == 1*/ }{
        assert0(y1);
        assert1(y2);
        assertEq(y1, y2);
    }

    public def mustFailRun(): boolean {
	f(0, 1);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test049_DynCheckException().execute();
    }

}
