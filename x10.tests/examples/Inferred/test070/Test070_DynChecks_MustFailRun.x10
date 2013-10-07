//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test070;

import harness.x10Test;

public class Test070_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	val b = new B(0);
	val a = new A(new B(1));
	Test070_DynChecks.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test070_DynChecks_MustFailRun().execute();
    }

}
