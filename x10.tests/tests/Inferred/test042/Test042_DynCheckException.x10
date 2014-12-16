//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test042_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test042_DynChecks.f(new Pair(1, 2), new Pair(1, 2));
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test042_DynCheckException().execute();
    }

}
