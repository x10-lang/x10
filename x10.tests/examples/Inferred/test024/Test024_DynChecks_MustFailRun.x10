//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test024;

import harness.x10Test;

public class Test024_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test024_DynChecks.f(false, 1, 1, 1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test024_DynChecks_MustFailRun().execute();
    }

}
