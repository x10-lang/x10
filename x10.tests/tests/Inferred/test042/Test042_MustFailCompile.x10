//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test042_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test042.f(new Pair(1, 2), new Pair(1, 2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test042_MustFailCompile().execute();
    }

}
