/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

//LIMITATION:
//Anonymous inner classes with new methods defined on them are not correctly handled.
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author vcave, vj
 */
public class CovariantReturnFromAnonymousClass extends x10Test {
	interface I  {
		 def test(): void;
		 def whatever():I;
	}
	
	public def run(): boolean {
	   x10.io.Console.OUT.println("" + (new I() {
		   public def test(): void {
			   x10.io.Console.OUT.println("Inner Class test invoked.");
		   }
		   public def whatever() = this;
	   }).whatever() + " hmm.. this worked");
	    return true;
	}
	public static def main(var args: Rail[String]): void {
		new CovariantReturnFromAnonymousClass().execute();
	}
}
