//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;
import x10.compiler.InferGuard;

public class Test003 extends x10Test {

    public def run(): boolean {
	val v1 = new Vec(42);
	val v2 = new Vec(42);
	Vec.add2(v1, v2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test003().execute();
    }

}
