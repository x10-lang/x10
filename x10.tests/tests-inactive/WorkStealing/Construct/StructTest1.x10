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
//OPTIONS: -WORK_STEALING=true

/*
 * A simple struct's concurrent method
 */
struct ST {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def conSum() {
	  val r:int;
      finish{
    	  async {r = x + y;} 
      }
	  return r;
  }
  
  public static final def conValue(){
	  val r:int;
      finish{
	     async {r = 1;} 
      }
      return r;
	  
  }
}

public class StructTest1 {
	
	public def run() {
		var passed:boolean = true;
	
		val st = ST(1,2);
		val r1 = st.conSum();
		passed &= (r1==3);
		Console.OUT.println("st.conSum() = " + r1);
		
		val r2 = ST.conValue();
		passed &= (r2==1);
		Console.OUT.println("st.conValue() = " + r2);
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new StructTest1().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}
