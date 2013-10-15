//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test034_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test034.f(1, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test034_MustFailCompile().execute();
    }

}
