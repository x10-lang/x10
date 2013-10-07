//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test022;

import harness.x10Test;

public class Test022_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test022_DynChecks.f(true, 1, 1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test022_DynChecks_MustFailRun().execute();
    }

}
