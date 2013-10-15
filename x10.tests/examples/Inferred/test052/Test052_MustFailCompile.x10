//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test052_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test052.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test052_MustFailCompile().execute();
    }

}
