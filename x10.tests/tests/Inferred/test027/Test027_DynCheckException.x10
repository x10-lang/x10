//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test027_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test027_DynChecks.f(false);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test027_DynCheckException().execute();
    }

}
