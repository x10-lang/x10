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
 * 
 *
 * @author vj
 */
public class EntailsPositive_MustFailCompile(i:int, j:int) extends x10Test {

	public def this(ii:int, jj:int):EntailsPositive_MustFailCompile = { property(ii,jj);}
	public def run():boolean = {
	    val x:EntailsPositive_MustFailCompile{self.i==1}  =  new EntailsPositive_MustFailCompile(1,2);
	    return true;
	}
	public static def main(Rail[String])= {
		new EntailsPositive_MustFailCompile(1,2).execute();
	}
}
