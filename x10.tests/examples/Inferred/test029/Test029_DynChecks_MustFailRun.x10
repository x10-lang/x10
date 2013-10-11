//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test029_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test029_DynChecks.f(1, 2, 3);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test029_DynChecks_MustFailRun().execute();
    }

}
