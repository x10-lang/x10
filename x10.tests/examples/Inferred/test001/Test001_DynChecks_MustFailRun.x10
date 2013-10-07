//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test001;

import harness.x10Test;

public class Test001_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test001_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test001_DynChecks_MustFailRun().execute();
    }


}
