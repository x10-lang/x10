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
 * Testing method invocation.
 *
 * @author vj 
 */
public class ParameterTypeIsNotObject4_MustFailCompile extends x10Test {
	class GenericWrapper[T] {
		  incomplete def m(x:Object):Void;
		  public def testAssign(y:T) {
			  m(y);
		  }
	}
	public def run()=true;

	public static def main(Rail[String]) {
		new ParameterTypeIsNotObject4_MustFailCompile().execute();
	}

  
}
