//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test003;

import harness.x10Test;

public class Test003_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	val v1 = new Vec(42);
	val v2 = new Vec(4012);
	Vec.add2(v1, v2);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test003_DynChecks_MustFailRun().execute();
    }

}
