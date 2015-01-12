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
    interface  I {
        public property i():int;
        public def a():void;
	}
	interface  J extends I{
        public property k():int;
        public def a():void;
	}
	
      class E(m:int, k:int) extends D implements J{
       public property k():int = k;
      public def this(mm:int, nn:int, ii:int, kk:int):E { 
          super(nn,ii); 
          property(mm, kk);
      }
      public def a():void = {
        var x:int;
      }
	}
	class D(n:int, i:int) implements I {
       public property i():int = i;
      public def this(nn:int, ii:int):D { property(nn, ii); }
      public def a():void= {
        var x:int;
      }
	}
	public def run(): boolean = {
        new E(1n,2n,3n,4n);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new InterfaceProp().execute();
	}
}
