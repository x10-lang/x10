import harness.x10Test;

/**
 * This class tests passing a class type to a method which 
 * specifies an interface type as a parameter.
 * The X10 2.0.3 compiler allows passing an unrelated type, but 
 * the program crashes at runtime with x10.lang.ClassCastException.
 * The code can be fixed by adding "implements I" at line 16.
 * @author milthorpe
 */
public class TestInterfaceParam_MustFailCompile extends x10Test {
    static interface I {
        public def isGood() : boolean;
    }

    static class C {
        public def isGood() : boolean = true;
    }

    public def doSomething(i : I) : boolean {
        return i.isGood();
    }

    public def run() : boolean {
    	// this must fail compilation. C is not declared to implement I.
        return doSomething(new C());  // ERR
    }

    public static def main(args : Rail[String]) {
        new TestInterfaceParam_MustFailCompile().execute();
    }
}

