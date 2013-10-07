//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test052;

import harness.x10Test;

public class Test052_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test052_DynChecks.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test052_DynChecks_MustFailRun().execute();
    }

}
