//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test046;

import harness.x10Test;

public class Test046_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test046_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test046_DynChecks_MustFailRun().execute();
    }

}
