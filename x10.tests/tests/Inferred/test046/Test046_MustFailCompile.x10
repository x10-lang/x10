//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test046_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test046.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test046_MustFailCompile().execute();
    }

}
