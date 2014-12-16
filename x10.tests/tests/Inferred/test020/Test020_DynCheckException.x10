/**
 * We do not want to infer this kind of constraint even if a solution exists (or many).
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test020_DynCheckException extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: Boolean, y1: Long, y2:Long){ y1==0 /*??<, y2==0 >??*/} {
        val z = b ? y1 : y2;
        assert0(z);
    }

    public def mustFailRun(): boolean {
	Test020_DynChecks.f(false, 0, 1);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test020_DynCheckException().execute();
    }

}
