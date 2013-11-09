/**
 * Inference of constraints on method parameters.
 *
 * Question of mutually recursive methods.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test012 extends x10Test {
    @InferGuard
    static def f (x: Long{ /*??<*/ /*>??*/ }) {
        g(x);
    }
    @InferGuard
    static def g (x: Long{ /*??<*/ /*>??*/ }) {
	if ( x == 0 ) { return; }
        f(x);
    }

    public def run(): boolean {
	Test012.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test012().execute();
    }

}
