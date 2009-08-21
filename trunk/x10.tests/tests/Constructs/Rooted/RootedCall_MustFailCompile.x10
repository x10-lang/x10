/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that rooted methods can only be called on rooted receivers.
 */
public class RootedCall_MustFailCompile extends x10Test {

    class A {
	var x:Int=1;
	rooted def m() {
	    x=2;
	}
    }

    public def run(): boolean = {
	var a: A = new A();
	a.m(); // must give an error.
	return true;
    }

    public static def main(Rail[String])  {
	new RootedCall().execute();
    }
}
