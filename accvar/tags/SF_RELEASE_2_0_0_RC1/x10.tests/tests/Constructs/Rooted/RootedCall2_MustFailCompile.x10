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
public class RootedCall2_MustFailCompile extends x10Test {

    class A {
	rooted val x =1;
	def m() { //must generate a compile-time error, since x is rooted.
	   val y = x;
    }
    }

    public def run(): boolean = {
	return true;
    }

    public static def main(Rail[String])  {
	new RootedCall2_MustFailCompile().execute();
    }
}
