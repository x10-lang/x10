//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

//SKIP_MANAGED_X10: XTENLANG-3328 
//SKIP_NATIVE_X10: XTENLANG-3328 

import harness.x10Test;

public class Test066_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test066.g(0, 0);
	Test066.f(false, 0, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test066_MustFailCompile().execute();
    }

}
