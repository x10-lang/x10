/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * It is ok to send in a value of type proto B when the method expects a proto A, if 
 * B <: A.
 * @author vj
 */
public class CovariantCall extends x10Test {

    class A {
    	var x: int;
    }
    class B extends A{
    	def this() {
    		CovariantCall.m(this);
    	}
    }
    	
    static def m(a: proto A) {
    	a.x = 5;
    }
    
    
    public def run() {
    	var b:B = new B();
    	return true;
    }

    public static def main(Rail[String])  {
	new CovariantCall().execute();
    }
}
