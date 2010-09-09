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

//LIMITATION:
// visibility checking not currently implemented.  And Nate, at
// least, thinks it never should be.

import harness.x10Test;

/**
 * A local variable referenced in a deptype in a method declaration must have 
 * greater or equal accessibility/visibility than the declaration.
 * 
 * This restriction probably is 4.4.3 "Constraint Semantics", in a static semantic 
 * rule called "Variable visibility"
 *
 * @author pvarma
 */
public class DepTypeVariableVisibility_MustFailCompile extends x10Test {
   private val traceOn = true;
   public def m(t: boolean(traceOn))
      : boolean(traceOn) 
      = t;
	public def run(): boolean = {
	   m(traceOn); 
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new DepTypeVariableVisibility_MustFailCompile().execute();
	}
}
