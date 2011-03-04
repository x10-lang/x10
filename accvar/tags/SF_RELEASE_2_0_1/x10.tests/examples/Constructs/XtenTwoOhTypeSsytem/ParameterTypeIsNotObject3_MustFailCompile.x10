/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A value of an unconstrained type parameter T cannot be assigned to a variable of type Object.
 * Testing  return value from a method.
 *
 * @author vj 
 */
public class ParameterTypeIsNotObject3_MustFailCompile extends x10Test {
	class GenericWrapper[T] {
		  public def testAssign(x:T):Object = x;
	}
	public def run()=true;

	public static def main(Rail[String]) {
		new ParameterTypeIsNotObject3_MustFailCompile().execute();
	}

  
}
