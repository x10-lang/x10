/*
 * Property methods
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;


public class Test074 extends x10Test {
    property eq0(x:Long) = x == 0;

    def assert0(x: Long){ eq0(x) }{}

    @InferGuard
    def f (y:Long{/*??< self==0 >??*/}) {
    	assert0(y);
    }

    public def run(): boolean {
	(new Test074()).f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test074().execute();
    }

}
