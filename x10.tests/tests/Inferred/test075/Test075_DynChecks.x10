/*
 * Property methods
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

class Prop {
    static property eq0(x:Long) = x == 0;
}


public class Test075_DynChecks extends x10Test {

    def assert0(x: Long){ Prop.eq0(x) }{}

    @InferGuard
    def f (y:Long{/*??< self==0 >??*/}) {
    	assert0(y);
    }

    public def run(): boolean {
	(new Test075_DynChecks()).f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test075_DynChecks().execute();
    }

}
