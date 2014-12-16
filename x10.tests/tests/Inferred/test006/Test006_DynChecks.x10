//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test006_DynChecks extends x10Test {

    public def run(): boolean {
	val v = new Vec(42);
	Vec.cp(v, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test006_DynChecks().execute();
    }

}
