//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test027_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test027.f(false);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test027_MustFailCompile().execute();
    }

}
