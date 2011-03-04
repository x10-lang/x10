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
import x10.lang.Runtime;




public class ClockedVar[T] implements ClockableVar{


    var xRead:T;
    val MAXWORKERS = 1050;
    val xWrite: Rail[T]! = Rail.make[T](MAXWORKERS);
    val op:(T,T)=>T;
    var opInit:T;
    var changed:Boolean;

    
   @NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
   @NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
    static class Lock {
        public native def this();

        public native def lock():Void;

        public native def tryLock():Void;

        public native def unlock():Void;

        public native def getHoldCount():Int;
    };
    
    
    
    //val lock = new Lock();

    public def this () {
				 			        op = null;
   }

    public def this (c: Clock!, oper: (T,T)=>T!, opInitial:T) {
		  
    	c.addClockedVar(this); 
     	op = oper;
     	opInit = opInitial;
     	changed = false;
     	var i: int;
     	for ( i = 0; i < MAXWORKERS; i++)	
     		xWrite(i) = opInitial;
     }
     
    public def this(c:Clock, oper: (T,T)=>T, opInitial:T, x:T)
     {
    	val clk = c as Clock!; 
    	xRead = x;
        clk.addClockedVar(this); 
        op = oper; 
        opInit = opInitial;
        changed = false; 
        var i: int;
     	for ( i = 0; i < MAXWORKERS; i++)	
     		xWrite(i) = opInitial;
      }
      
    public @Inline @Header def get$G():ClockedVar[T] = this;
   

    public @Inline @Header def getClocked():T = xRead;
    




    public @Inline @Header def setClocked(x:T) {
        	val i = Runtime.workerTid();
	        changed = true;
        this.xWrite(i) = op(xWrite(i), x);
    } 
    
    public @Inline @Header def setR(x:T) {
	       this.xRead=x;
   }
    

    public def move(): Void {
        if (changed) {
        	this.xRead = this.xWrite(0);
        	this.xWrite(0) = opInit;
		val numOfWorkers = Runtime.numOfWorkers() + 10;
        	//Console.OUT.println("Worker id" + numOfWorkers);
		         var i: int;
        	for (i = 1; i < numOfWorkers; i++) {
        		this.xRead =  op (this.xRead, this.xWrite(i));
        		this.xWrite(i) = opInit;
         	}
         	this.changed = false;
        } 
    	
    }

    
}
