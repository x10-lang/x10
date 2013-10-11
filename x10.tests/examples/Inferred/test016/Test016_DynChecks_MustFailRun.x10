//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test016_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test016_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test016_DynChecks_MustFailRun().execute();
    }

}
