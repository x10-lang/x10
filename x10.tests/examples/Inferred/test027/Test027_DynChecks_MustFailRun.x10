//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test027;

import harness.x10Test;

public class Test027_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test027_DynChecks.f(false);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test027_DynChecks_MustFailRun().execute();
    }

}
