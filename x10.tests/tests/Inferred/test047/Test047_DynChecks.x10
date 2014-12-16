/**
 * Variation on the example Test45.
 *
 * Useless constraint on a local variable and a false dependance to this avriable.
 * The useless constraint is between local variable.
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test047_DynChecks extends x10Test {
    static def assert0(x: Long{ self == 0 }, dummy: Long){}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z : Long = 42;
        val zz : Long {self == z} = z;
        assert0(y, zz);
    }

    public def run(): boolean {
	Test047_DynChecks.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test047_DynChecks().execute();
    }

}
