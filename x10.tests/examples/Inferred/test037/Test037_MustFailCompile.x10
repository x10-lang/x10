//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test037_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test037.f(2, new Pair(1,1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test037_MustFailCompile().execute();
    }

}
