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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that properties may be declared on interfaces.
 *
 * @author raj
 */
public class InterfaceProp extends x10Test {
interface  I(i:int) {
      public def a():void;
	}
	interface  J(k:int) extends I{
      public def a():void;
	}
	
	class E(m:int, k:int) extends D implements J{
      public def this(mm:int, nn:int, ii:int, kk:int):E { super(nn,ii); m = mm; k = kk;}
      public def a():void = {
        var x:int;
      }
	}
	class D(n:int, i:int) implements I {
      public def this(nn:int, ii:int):D { n = nn; i=ii; }
      public def a():void= {
        var x:int;
      }
	}
	public def run(): boolean = {
        new E(1,2,3,4);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new InterfaceProp().execute();
	}
}
