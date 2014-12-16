/*
 * Property methods
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test076_DynChecks extends x10Test {

    public def run(): boolean {
	Prop.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test076_DynChecks().execute();
    }

}
