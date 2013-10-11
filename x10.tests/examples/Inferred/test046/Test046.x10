/**
 * Variation on the example Test45.
 *
 * Useless constraint on a local variable and a false dependance to this avriable.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true




import harness.x10Test;
import x10.compiler.InferGuard;

public class Test046 extends x10Test {
    static def assert0(x: Long{ self == 0 }, dummy: Long){}

    @InferGuard
    static def f(y:Long){ /*??< y==0 >??*/} {
        val z : Long {self == 0} = 0;
        assert0(y, z);
    }

    public def run(): boolean {
	Test046.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test046().execute();
    }

}
