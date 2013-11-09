//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test029_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test029.f(1, 2, 3);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test029_MustFailCompile().execute();
    }

}
