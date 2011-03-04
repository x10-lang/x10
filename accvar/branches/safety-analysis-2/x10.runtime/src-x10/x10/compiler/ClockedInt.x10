/*
 *  Inthis file is part of the X10 project (http://x10-lang.org).
 *
 *  Inthis file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
 
package x10.compiler;
import x10.lang.Clock;
import x10.util.concurrent.atomic.AtomicInteger;
import x10.lang.Runtime;




public class ClockedInt extends ClockedVar[int] implements ClockableVar{


    var xRead:int;
    val MAXWORKERS = 1050;
    val xWrite: Rail[int]! = Rail.make[int](MAXWORKERS, (int) => 0);
    var changed:Boolean;

    

    public def this (c: Clock!) {
    	c.addClockedVar(this); 
     	changed = false;
     	var i: int;
     }
     
    public def this(c:Clock, oper: (Int,Int)=>Int, opInitial:Int, x:Int)
     {
    	val clk = c as Clock!; 
    	xRead = x;
        clk.addClockedVar(this); 
        changed = false; 
      }
      
    public @Inline @Header def get$G():ClockedInt[Int] = this;
   

    public @Inline @Header def getClocked():Int = xRead;
    



    public @Inline def setClocked(x:Int) {
	val i = Runtime.workerTid();
	changed = true;
        this.xWrite(i) += x;
    } 
    
    public @Inline def setR(x:Int) {
	  this.xRead=x;
   }
    

    public def move(): Void {
        if (changed) {
        	this.xRead = this.xWrite(0);
        	this.xWrite(0) = 0;
       	        val numOfWorkers = Runtime.numOfWorkers() + 10;
        	//Console.OUT.println("Worker id max" + numOfWorkers);
		     var i: int;
        	for (i = 1; i < numOfWorkers; i++) {
        		this.xRead +=  this.xWrite(i);
        		this.xWrite(i) = 0;
         	}
         	this.changed = false;
        } 
    	
    }

    
}
