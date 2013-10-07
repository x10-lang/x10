//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test033;

import harness.x10Test;

public class Test033_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test033_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test033_DynChecks_MustFailRun().execute();
    }

}
