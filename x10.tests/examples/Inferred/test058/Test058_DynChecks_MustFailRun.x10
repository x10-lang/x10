//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test058;

import harness.x10Test;

public class Test058_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test058_DynChecks.f(1, 2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test058_DynChecks_MustFailRun().execute();
    }

}
