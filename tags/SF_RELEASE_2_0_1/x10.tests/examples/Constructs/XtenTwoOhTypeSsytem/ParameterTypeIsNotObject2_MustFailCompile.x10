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
 * Testing assignment to a field.
 *
 * @author vj 
 */
public class ParameterTypeIsNotObject2_MustFailCompile extends x10Test {
	class GenericWrapper[T] {
		  var dummy:Object = null;
		  public def testAssign(x:T) {
		  // bad!!
		    dummy = x;
		  }
		}
	
	public def run()=true;

	public static def main(Rail[String]) {
		new ParameterTypeIsNotObject2_MustFailCompile().execute();
	}

  
}
