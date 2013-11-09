//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test006_DynCheckException extends x10Test {

    public def mustFailRun(): boolean {
	val v = new Vec(42);
	Vec.cp(v, 4012);
        return true;
    }

    public def run() {
        try { mustFailRun(); return false; } catch (FailedDynamicCheckException) {}
        return true;
    }

    public static def main(Rail[String]) {
    	new Test006_DynCheckException().execute();
    }

}
