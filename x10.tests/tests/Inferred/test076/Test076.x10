/*
 * Property methods
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test076 extends x10Test {

    public def run(): boolean {
	Prop.f(0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test076().execute();
    }

}
