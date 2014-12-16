//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test003_MustFailCompile extends x10Test {

    public def run(): boolean {
	val v1 = new Vec(42);
	val v2 = new Vec(4012);
	Vec.add2(v1, v2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test003_MustFailCompile().execute();
    }

}
