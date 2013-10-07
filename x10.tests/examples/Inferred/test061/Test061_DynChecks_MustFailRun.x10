//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test061;

import harness.x10Test;

public class Test061_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test061_DynChecks.g(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test061_DynChecks_MustFailRun().execute();
    }

}
