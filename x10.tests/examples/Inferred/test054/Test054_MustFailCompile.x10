//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test054;

import harness.x10Test;

public class Test054_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test054.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test054_MustFailCompile().execute();
    }

}
