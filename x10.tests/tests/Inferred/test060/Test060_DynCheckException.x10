//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test060_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test060_DynChecks.f(1, 2);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test060_DynCheckException().execute();
    }

}
