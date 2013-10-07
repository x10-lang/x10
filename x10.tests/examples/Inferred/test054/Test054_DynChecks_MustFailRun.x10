//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test054;

import harness.x10Test;

public class Test054_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test054_DynChecks.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test054_DynChecks_MustFailRun().execute();
    }

}
