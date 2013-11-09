//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test076_MustFailCompile extends x10Test {

    public def run(): boolean {
	Prop.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test076_MustFailCompile().execute();
    }

}
