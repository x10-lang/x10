/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that a guarded function can be called with args that satisfy the guard.
 *
 * @author vj 10/2009
 */
public class GuardedFunctionInvocation_MustFailCompile extends x10Test {

	val f = (x:Int){x==3}=>x;
	
	public def run() = f(4)==3;

	public static def main(Rail[String]) {
		new GuardedFunctionInvocation_MustFailCompile().execute();
	}
}
