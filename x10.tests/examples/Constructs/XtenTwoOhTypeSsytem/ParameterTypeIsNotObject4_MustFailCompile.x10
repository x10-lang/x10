/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * A value of an unconstrained type parameter T cannot be assigned to a variable of type Empty.
 * Testing method invocation.
 *
 * @author vj 
 */
public class ParameterTypeIsNotObject4_MustFailCompile extends x10Test {
	class GenericWrapper[T] {
		  def m(x:Empty):void = {}
		  public def testAssign(y:T) {
			  m(y); // ERR: Method m(x: x10.lang.Empty): void in ParameterTypeIsNotObject4_MustFailCompile.GenericWrapper{self==ParameterTypeIsNotObject4_MustFailCompile.GenericWrapper#this} cannot be called with arguments (T{self==y});    Invalid Parameter.
		  }
	}
	public def run()=true;

	public static def main(Rail[String]) {
		new ParameterTypeIsNotObject4_MustFailCompile().execute();
	}

  
}
