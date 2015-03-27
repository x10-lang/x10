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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Inlined Instanceof checking code must ensures both type and constraint equality is checked !
 * Issue: Types are not compatible but constraint are verified.
 * @author vcave
 **/
public class NotInstanceof1_Inline extends x10Test {
	 
	public def run():boolean {
		val diffType = this.getDifferentType();
		
		return !(diffType instanceof X10DepTypeClassOneB{p==1});
	}

	private def getSameType():Any {
		return new X10DepTypeClassOneB(1);
	}
	
	private def getDifferentType():Any {
		return new OtherClass(1);
	}
	
	public static def main(args: Rail[String]) {
		new NotInstanceof1_Inline().execute();
	}
		 
	 public class OtherClass (p:long) {
		public  def this(p:long) {
                    property(p);
		}
	 }
}
