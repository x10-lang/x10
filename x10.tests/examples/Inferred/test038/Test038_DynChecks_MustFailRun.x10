//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test038;

import harness.x10Test;

public class Test038_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test038_DynChecks.f(new Elt(1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test038_DynChecks_MustFailRun().execute();
    }

}
