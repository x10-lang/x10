//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test044;

import harness.x10Test;

public class Test044_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test044_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test044_DynChecks_MustFailRun().execute();
    }

}
