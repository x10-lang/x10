//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test041_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test041.f(new Pair(1,2), new Pair(2, 3));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test041_MustFailCompile().execute();
    }

}
