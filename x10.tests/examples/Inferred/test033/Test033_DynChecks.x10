/**
 * Example that use assignement.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test033_DynChecks extends x10Test {

    @InferGuard
    static def f(x: Long) { /*??< x == 0 >??*/ } {
        val v : Long{self == 0} = 0;
        var y : Long{self == v};
        y = x;
    }

    public def run(): boolean {
	Test033_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test033_DynChecks().execute();
    }

}
