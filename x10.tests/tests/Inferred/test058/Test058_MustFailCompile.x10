//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test058_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test058.f(1, 2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test058_MustFailCompile().execute();
    }

}
