//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test074;

import harness.x10Test;

public class Test074_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	(new Test074_DynChecks()).f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test074_DynChecks_MustFailRun().execute();
    }

}
