//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test038_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test038.f(new Elt(1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test038_MustFailCompile().execute();
    }

}
