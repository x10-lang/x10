/**
 * Handling of local variable.
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test050 extends x10Test {
    static def assert0(x: Long{ self == 0 }){}
    static def assert1(x: Long{ self == 1 }){}
    static def assertEq(a: Long, b: Long){ a == b } {}

    @InferGuard
    static def f(f_arg:Long){/*??< f_arg == 0 >??*/ } {
        val y1: Long{self == 0};
        val y2 = 0;
        y1 = y2;
        assertEq(f_arg, y1);
    }

    public def run(): boolean {
	Test050.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test050().execute();
    }

}
