//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test074_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	(new Test074_DynChecks()).f(1);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test074_DynCheckException().execute();
    }

}
