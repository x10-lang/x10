//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test078_MustFailCompile extends x10Test {

    public def run(): boolean {
	val v1 = new Vec(42);
	val v2 = new Vec(42);
	val v3 = new Vec(4012);
	Vec.add3(v1, v2, v3);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test078_MustFailCompile().execute();
    }

}
