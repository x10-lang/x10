//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test036_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	Test036_DynChecks.f(new Pair(1,2));
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test036_DynCheckException().execute();
    }

}
