//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test010_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test010.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test010_MustFailCompile().execute();
    }

}
