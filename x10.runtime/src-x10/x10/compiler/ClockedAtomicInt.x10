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
 
package x10.compiler;
import x10.lang.Clock;
import x10.util.concurrent.atomic.AtomicInteger;



public class ClockedAtomicInt extends ClockedVar[Int] implements ClockableVar {

  
    var xRead: int = 0;
    val xWrite: AtomicInteger! = new AtomicInteger(0);
    var changed:Boolean = false;

    

    public def this (c: Clock!, oper: (Int,Int)=>Int, opInitial:Int) {
    	c.addClockedVar(this); 	
     
     }
     
    public def this(c:Clock, oper: (Int,Int)=>Int, opInitial:Int, x:Int)
     {
    	val clk = c as Clock!; 
    	xRead = x;
        clk.addClockedVar(this); 
	}
	
	
	public @Inline @Header def get$G():ClockedAtomicInt[Int] = this;
    
      
    public @Inline def getClocked():Int {
    	  return xRead;
   }



    public @Inline def setClocked(x:Int) {
       // Console.OUT.println("setting");
    	changed = true;
		xWrite.addAndGet(x);
    

    } 
    
    public @Inline def setR(x:Int){xRead = x;}
    
    public @Inline def getW(){return xWrite;}
    

    public @Inline def move(): Void {
        if (changed)
        	this.xRead = this.xWrite.get(); 
     	this.xWrite.set(0);
    	this.changed = false;
    }

    
}