//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test041;

import harness.x10Test;

public class Test041_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test041_DynChecks.f(new Pair(1,2), new Pair(2, 3));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test041_DynChecks_MustFailRun().execute();
    }

}
