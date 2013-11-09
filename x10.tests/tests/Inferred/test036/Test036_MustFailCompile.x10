//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test036_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test036.f(new Pair(1,2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test036_MustFailCompile().execute();
    }

}
