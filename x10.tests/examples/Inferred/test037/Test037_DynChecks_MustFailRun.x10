//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test037_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test037_DynChecks.f(2, new Pair(1,1));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test037_DynChecks_MustFailRun().execute();
    }

}
