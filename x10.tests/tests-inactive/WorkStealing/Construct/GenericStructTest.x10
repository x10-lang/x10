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

//Test struct with generics

struct Pair[X,Y] {
  val x:X;
  val y:Y;

  public def this(a:X, b:Y) { x = a; y = b; }

  public def conFirst(){
	  val r:X;
  	  finish{
  		  async {r = x;}
  	  }  
	  return r;
  }
  public def conSecond(){
	  val r:Y;
	  finish{
		  async {r = y;}
	  }  
      return r;
  }
  public static def conDo[T](c:T){
	  val r:T;
      finish{
	     async {r = c;}
      }  
      return r;  
  }
}

public class GenericStructTest {
	
	public def run() {
		var passed:boolean = true;
	
		val pair = Pair[Int, String](1, "hello");
		val r1 = pair.conFirst();
		passed &= (r1==1);
		Console.OUT.println("pair.conFirst() = " + r1);
		
		val r2 = pair.conSecond();
		passed &= (r2.equals("hello"));
		Console.OUT.println("pair.conSecond() = " + r2);
		
		
		val r3 = Pair.conDo[String]("ABC");
		passed &= (r3.equals("ABC"));
		Console.OUT.println("Pair.conDo[String]() = " + r3);
		
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new GenericStructTest().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}
