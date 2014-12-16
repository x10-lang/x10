//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test018_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test018_DynChecks.f(1);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test018_DynCheckException().execute();
    }

}
