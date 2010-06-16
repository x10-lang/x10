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



public class ClockedAtomicInt implements ClockableVar {

  
    val xRead:AtomicInteger! = new AtomicInteger();
    val xWrite: AtomicInteger! = new AtomicInteger();
    val opInit:Int;
    var changed:Boolean;

    

    public def this (c: Clock!, opInitial:Int) {
    	c.addClockedVar(this); 
     	opInit = opInitial;
     	changed = false;	
     	xWrite.set(opInitial);
     }
     
    public def this(c:Clock, opInitial:Int, x:Int)
     {
    	val clk = c as Clock!; 
    	xRead.set(x);
        clk.addClockedVar(this); 
        opInit = opInitial;
        changed = false;
        xWrite.set(opInitial); 
      }
      
    public def get():Int {
    	  return xRead.get();
   }



    public def set(x:Int) {
    	changed = true;
		xWrite.addAndGet(x);
    

    } 
    
    public def setR(x:Int){xRead.set(x);}
    
    public def getW(){return xWrite;}
    

    public def move(): Void {
        if (changed)
        	this.xRead.set(this.xWrite.get()); 
     	this.xWrite.set(opInit);
    	this.changed = false;
    }

    
}