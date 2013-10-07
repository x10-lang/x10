//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test015;

import harness.x10Test;

public class Test015_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test015_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test015_DynChecks_MustFailRun().execute();
    }

}
