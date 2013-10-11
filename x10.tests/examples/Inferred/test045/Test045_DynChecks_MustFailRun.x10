//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test045_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test045_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test045_DynChecks_MustFailRun().execute();
    }

}
