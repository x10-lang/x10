//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test057;

import harness.x10Test;

public class Test057_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test057_DynChecks.f(1, 2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test057_DynChecks_MustFailRun().execute();
    }

}
