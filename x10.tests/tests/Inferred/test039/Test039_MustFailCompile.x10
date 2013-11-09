//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test039_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test039.f(new Pair(0, 1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test039_MustFailCompile().execute();
    }

}
