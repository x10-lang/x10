//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test011;

import harness.x10Test;

public class Test011_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test011_DynChecks.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test011_DynChecks_MustFailRun().execute();
    }

}
