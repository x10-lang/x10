//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test072_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	val b = new B(0);
	val a = new A(new B(0));
	Test072_DynChecks.f(a, b);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test072_DynCheckException().execute();
    }

}
