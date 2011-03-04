/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A rooted method can be called on a rooted receiver.
 */
public class RootedCall extends x10Test {

    class A {
	var x:Int=1;
	rooted def m() {
	    x=2;
	}
    }

    public def run(): boolean = {
	var a: rooted A = new A();
	a.m(); // ok
	return true;
    }

    public static def main(Rail[String])  {
	new RootedCall().execute();
    }
}
