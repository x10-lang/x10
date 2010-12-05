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



public class ClockedRail[T] extends ClockedRailBase[T] implements ClockableVar  {

  
    var xRead: Rail[T]!;
    var xWrite: Rail[T]!;
    val op:(T,T)=>T;
    val opInit:T;
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
    
    
    
    val locks: Rail[Lock];


    public def this (length: int, c: Clock!, oper: (T,T)=>T!, opInitial:T) {
	super(length);
	this.length = length; 
    	c.addClockedVar(this); 
    	xRead = Rail.make[T] (length, (i:int) => opInitial);
    	xWrite = Rail.make[T] (length, (i:int) => opInitial);
	locks = Rail.make[Lock] (length, (i:int) => new Lock());
     	op = oper;
     	opInit = opInitial;
     	changed = false;	
     	//xWrite = opInitial;
     }
    
    public def this(length: int, c:Clock!, oper: (T,T)=>T, opInitial:T,  init: (int) => T)
     {
	super(length);
	this.length = length; 
    	val clk = c as Clock!; 
    	xRead = Rail.make[T] (length, init);
    	xWrite = Rail.make[T] (length, (i: int) => opInitial);
	locks = Rail.make[Lock] (length, (i:int) => new Lock());

        c.addClockedVar(this); 
        op = oper; 
        opInit = opInitial;
        changed = false;
        //xWrite = opInitial; 
      }
      
    public def getClocked(index: int):T {
	  //Console.OUT.println("Reading" + this.xRead(index));
    	  return xRead(index);
   }



    public def setClocked(index:int, x:T) {
    	changed = true;
        (locks(index) as Lock!).lock();
        this.xWrite(index) = op(this.xWrite(index), x);
        (locks(index) as Lock!).unlock();
 	//Console.OUT.println("Writing"); 
    } 
    
    public def setR(index: int, x:T){this.xRead(index)=x;}
    
    public def getW(index: int){return xWrite(index);}
    

    public def move(): Void {
        if (changed) {
		var i: int = 0;
		for (i = 0; i < length; i++) {
      	  		this.xRead(i) = this.xWrite(i);
			this.xWrite(i) = opInit;
		}

	}
    	this.changed = false;
    }

    
}
