/**
 * Example that use assignement.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test032 extends x10Test {
    @InferGuard
    static def f(x: Long) { /*??< x == 0 >??*/ } {
        var y : Long{self == 0};
        y = x;
    }

    public def run(): boolean {
	Test032.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test032().execute();
    }

}
