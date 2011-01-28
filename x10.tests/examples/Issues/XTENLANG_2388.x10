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
 * @author yzibin 1/2011
 */
class XTENLANG_2388 extends x10Test {
	static struct S1 {}
	static struct S2(i:Int,j:Double) {}
	static struct S3(i:Int{self!=0},j:Double) {}
  
  public def run(): boolean {
	  chk(Int haszero); // ShouldNotBeERR
	  chk(!(Int{self!=0} haszero)); // ShouldNotBeERR
	  chk((Int{self!=1} haszero)); // ShouldNotBeERR
	  chk((Int{self==0} haszero)); // ShouldNotBeERR
	  chk(!(Int{self==1} haszero)); // ShouldNotBeERR
	  chk(Char haszero); // ShouldNotBeERR
	  chk(Double haszero); // ShouldNotBeERR
	  chk(Float haszero); // ShouldNotBeERR
	  chk(Byte haszero); // ShouldNotBeERR
	  chk(Short haszero); // ShouldNotBeERR
	  chk(Long haszero); // ShouldNotBeERR
	  chk(UByte haszero); // ShouldNotBeERR
	  chk(UShort haszero); // ShouldNotBeERR
	  chk(UInt haszero); // ShouldNotBeERR
	  chk(ULong haszero); // ShouldNotBeERR

	  chk(Any haszero); // ShouldNotBeERR
	  chk(!(Any{self!=null} haszero)); // ShouldNotBeERR
	  chk(String haszero); // ShouldNotBeERR
	  chk(String{self==null} haszero); // ShouldNotBeERR
	  chk(!(String{self!=null} haszero)); // ShouldNotBeERR
	  chk(Object haszero); // ShouldNotBeERR
	  
	  chk(S1 haszero); // ShouldNotBeERR	  
	  chk(S2 haszero); // ShouldNotBeERR	  
	  chk(!(S3 haszero)); // ShouldNotBeERR  
	  chk(!(S2{i!=0} haszero)); // ShouldNotBeERR

      return true;
  }
  
  
  public static def main(Array[String](1)) {
    new XTENLANG_2388().execute();
  }
}
