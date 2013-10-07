//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test036;

import harness.x10Test;

public class Test036_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test036_DynChecks.f(new Pair(1,2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test036_DynChecks_MustFailRun().execute();
    }

}
