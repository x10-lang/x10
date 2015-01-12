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
 * Check you can offer the result of a method invocation, where the reducer is of a generic type.
 * @author vj
 */
public class CF2Generic extends x10Test {

    static struct Reducer implements Reducible[Long] {
     	public  def zero()=0;
     	public  operator this(a:Long,b:Long)=a+b;
    }
    static class C[T] {
    	def m(b:T)=b;
        def m(a:Reducible[T], b:T):T {
      	  val x = finish(a) {
      		  offer m(b);
      		  offer m(b);
      	  };
      	  return x;
        }
        
  	}
	public def run() = 6==new C[Long]().m(Reducer(), 3);

	public static def main(Rail[String]) {
		new CF2Generic().execute();
	}
}
