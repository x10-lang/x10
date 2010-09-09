/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 * Test that constraints are correctly propagated through when a field's receiver is an expression e.
 * The expression may be of type Foo!. If the field Foo.f is declared of type Fum!, then it must be the
 * case that e.f's home is statically known to be here.

 * @author vj
 */
public class FieldReceiverIsExpr extends x10Test {
    
	class F {
		val f:F!;
	        def m(){}
	        def this(f:F!) { this.f=f;}
	}
	    def m() { 
	        (new F(null) as F!).f.m();
	    }

    public def run() = true;

    public static def main(Rail[String]) {
	  new FieldReceiverIsExpr().execute();
    }

}
