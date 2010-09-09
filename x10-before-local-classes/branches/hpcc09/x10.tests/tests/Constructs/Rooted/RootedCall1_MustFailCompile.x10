/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that mutable field of this are accesssed only from rooted methods.
 */
public class RootedCall1_MustFailCompile extends x10Test {

    class A {
	var x:Int =1;
	def m() { //must generate a compile-time error, since x is implicitly rooted.
	   val y = x;
    }
    }

    public def run() {
	return true;
    }

    public static def main(Rail[String])  {
	new RootedCall1_MustFailCompile().execute();
    }
}
