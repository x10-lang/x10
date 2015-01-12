/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vj
 */
public class FieldsMustBeFinalInDepClause extends x10Test {
	class Test(i:int, c:boolean) {
	   val b:Boolean(c);
	   public def this(ii:int, bb:boolean):Test = {
	     property(ii,bb);
	     b=bb as Boolean(c); // necessary, typechecker does not know this is true.
	   }
	   def m():Test{self.c==this.b}=this;
	}
	
	public def run(): boolean = {
	   val a= new Test(52n,true);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new FieldsMustBeFinalInDepClause().execute();
	}
}
