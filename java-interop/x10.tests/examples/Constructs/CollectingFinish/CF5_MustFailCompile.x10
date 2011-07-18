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
 *  Check that you cannot offer a type that is not a subtype of the offer type.
 * @author vj
 */
public class CF5_MustFailCompile extends x10Test {

    static struct Reducer implements Reducible[Int] {
     	public  def zero()=0;
     	public  operator this(a:Int,b:Int)=a+b;
    }
    def m(a:Any):void offers Int(6) {
    	offer a; // ERR
    }
	public def run() {

		val x = finish (Reducer()){
			m(6);
		};
		return 6==x;
	}

	public static def main(Array[String](1)) {
		new CF5_MustFailCompile().execute();
	}
}
