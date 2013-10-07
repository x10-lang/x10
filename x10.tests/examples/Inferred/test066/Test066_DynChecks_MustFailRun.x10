//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test066;

import harness.x10Test;

public class Test066_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test066_DynChecks.g(0, 0);
	Test066_DynChecks.f(false, 0, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test066_DynChecks_MustFailRun().execute();
    }

}
