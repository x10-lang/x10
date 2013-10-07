//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test030;

import harness.x10Test;

public class Test030_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test030_DynChecks.f(1, 1, 2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test030_DynChecks_MustFailRun().execute();
    }

}
