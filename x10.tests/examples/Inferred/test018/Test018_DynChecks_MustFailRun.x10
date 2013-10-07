//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test018;

import harness.x10Test;

public class Test018_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test018_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test018_DynChecks_MustFailRun().execute();
    }

}
