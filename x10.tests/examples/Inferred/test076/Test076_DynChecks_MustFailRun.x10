//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test076;

import harness.x10Test;

public class Test076_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Prop.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test076_DynChecks_MustFailRun().execute();
    }

}
