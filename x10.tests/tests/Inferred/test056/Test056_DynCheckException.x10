//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test056_DynCheckException extends x10Test {

    static def assertEq(a: Long, b: Long){ a == b } {}
    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f (x: Long, y: Long) {
	val v1: Long{self == 0} = x;
	val v2: Long{self != 0} = y;
	assertEq(x, y);
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
    	new Test056_DynCheckException().execute();
    }

}
