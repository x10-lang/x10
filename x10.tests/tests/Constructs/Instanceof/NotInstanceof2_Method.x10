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

import harness.x10Test;

/**
 * Purpose: Side Effect Instanceof checking code must ensures both type and c
 * onstraint equality is checked !
 * Issue: Types are not compatible but constraint are verified.
 * @author vcave
 **/
public class NotInstanceof2_Method extends x10Test {
	 
	public def run():boolean = 
	 !(this.getDifferentType() instanceof X10DepTypeClassOneB{p==1});
	
	private def getSameType():Any = new X10DepTypeClassOneB(1);
	
	private def getDifferentType():Any =  new OtherClass(1);
	
	public static def main(Rail[String]) = {
		new NotInstanceof2_Method().execute();
	}
		 
	 public class OtherClass (p:long) {
		public def this(p:long)= {
                    property(p);
		}
	 }
}
