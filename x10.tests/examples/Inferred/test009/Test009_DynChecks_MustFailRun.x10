//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test009;

import harness.x10Test;

public class Test009_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test009_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test009_DynChecks_MustFailRun().execute();
    }

}
