//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test024_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test024.f(false, 1, 1, 1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test024_MustFailCompile().execute();
    }

}
