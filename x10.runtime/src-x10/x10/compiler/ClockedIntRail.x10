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



public class ClockedIntRail[T] extends ClockedRailBase[Int] implements ClockableVar  {

  
    var xRead: Rail[int]!;
    var xWrite: Rail[AtomicInteger]!;
    val opInit:int;
    var changed:Boolean;
    val length:int;
    
    
    
    
   @NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
   @NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
    static class Lock {
        public native def this();

        public native def lock():Void;

        public native def tryLock():Void;

        public native def unlock():Void;

        public native def getHoldCount():Int;
    };
    
    
    
 


    public def this (length: int, c: Clock!, opInitial:int) {
	super(length);
	this.length = length; 
    	c.addClockedVar(this); 
    	xRead = Rail.make[Int] (length, (i:int) => opInitial);
    	xWrite = Rail.make[AtomicInteger] (length, (i:int) => new AtomicInteger(opInitial));
     	opInit = opInitial;
     	changed = false;	
     	//xWrite = opInitial;
     }
    
    public def this(length: int, c:Clock!, opInitial:int,  init: (int) => int)
     {
	super(length);
	this.length = length; 
    	val clk = c as Clock!; 
    	xRead = Rail.make[Int] (length, init);
    	xWrite = Rail.make[AtomicInteger] (length, (i: int) => new AtomicInteger(opInitial));

        c.addClockedVar(this); 
        opInit = opInitial;
        changed = false;
        //xWrite = opInitial; 
      }
      
    public @Inline @Header def getClocked(index: int):int {
	  //Console.OUT.println("Reading" + (this.xRead(index) as AtomicInteger!).get());
    	  return (xRead(index));
   }



    public @Inline @Header def setClocked(index:int, x:int) {
    	changed = true;
        (this.xWrite(index) as AtomicInteger!).addAndGet(x);
    } 
    
    public def setR(index: int, x:int){this.xRead(index) = x;}
    
    public def getW(index: int){return (xWrite(index) as AtomicInteger!).get();}
    

    public @Inline @Header def move(): Void {
        if (changed) {
		var i: int = 0;
		for (i = 0; i < length; i++) {
        		this.xRead(i) = (this.xWrite(i) as AtomicInteger!).get();
			(this.xWrite(i) as AtomicInteger!).set(opInit);
		}		
	}
    	this.changed = false;
    }

    
}
