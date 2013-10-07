//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test034;

import harness.x10Test;

public class Test034_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test034_DynChecks.f(1, 42);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test034_DynChecks_MustFailRun().execute();
    }

}
