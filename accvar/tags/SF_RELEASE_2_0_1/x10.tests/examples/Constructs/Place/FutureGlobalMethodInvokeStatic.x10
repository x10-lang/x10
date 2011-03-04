// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * @author bdlucas
 */

public class FutureGlobalMethodInvokeStatic extends x10Test {

    static class C[S] {
        property p:int = 0;
        val x:S;
        var y:S;
        def foo() {}
        global def foo(x:S) {}
        final def foo[T](x:T) {}
        def this(s:S) {
            x = s;
            y = s;
        }
    }

    val c = new C[String]("0");

    public def run02(): boolean = {
    		val p = Place.places(1);
    		val cc = this.c;
            val f = future (p) {
            	// ok to invoke a global method.
                cc.foo("1");
            return true;
        };
        return f.force();
    }

    public def run(): boolean {
    	if (Place.MAX_PLACES == 1) {
    		x10.io.Console.OUT.println("not enough places to run this test");
    		return false;
    	}
    	return run02();
	}

    public static def main(Rail[String]) {
        new FutureGlobalMethodInvokeStatic().execute();
    }
}
