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




public class ClockedOpLessVar[T] extends ClockedVar[T]  implements ClockableVar{


    var xRead:T;
    var xWrite:T;
    val changed: AtomicInteger! = new AtomicInteger(0);
    

    
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
				 			  
   }
    public def this (c: Clock!) {
    
    	  c.addClockedVar(this); 
     }
     

    public def this (c: Clock!, oper: (T,T)=>T!, opInitial:T) {
    
    	  c.addClockedVar(this); 
     }
     
    public def this(c:Clock, oper: (T,T)=>T, opInitial:T, x:T)
     {
    	  val clk = c as Clock!; 
          xRead = x;
          clk.addClockedVar(this); 
      }
      
    public @Inline @Header def get$G():ClockedOpLessVar[T] = this;
   

    public @Inline @Header def getClocked():T = xRead;
    


    public @Inline @Header def setClocked(x:T) {
	    changed.incrementAndGet();
        	xWrite = x;
//	Console.OUT.println("Changing");
    } 
    
    public @Inline @Header def setR(x:T) {
	       this.xRead=x;
   }
    

    public def move(): Void {
        if (changed.get() == 1) {
		            xRead = xWrite;
         	   
        } 
	//Console.OUT.println("Get" + changed.get());

        changed.set(0);
    	
    }	

    
}
