//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test029_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test029_DynChecks.f(1, 2, 3);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test029_DynCheckException().execute();
    }

}
