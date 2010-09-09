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



public class ClockedRail[T] implements ClockableVar{

  
    var xRead: Rail[T]!;
    var xWrite: Rail[T]!;
    val op:(T,T)=>T;
    val opInit:T;
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
    
    
    
    val lock = new Lock();



    public def this (c: Clock!, oper: (T,T)=>T!, opInitial:T, length:int) {
     
    	//c.addClockedVar[T](this); 
    	xRead = Rail.make[T] (length);
    	xWrite = Rail.make[T] (length);
     	op = oper;
     	opInit = opInitial;
     	changed = false;	
     	//xWrite = opInitial;
     }
     
    public def this(c:Clock, oper: (T,T)=>T, opInitial:T, length:int, x:T)
     {
    	val clk = c as Clock!; 
    	xRead = Rail.make[T] (length);
    	xWrite = Rail.make[T] (length, (i: int) => opInitial);

        //clk.addClockedVar(this); 
        op = oper; 
        opInit = opInitial;
        changed = false;
        //xWrite = opInitial; 
      }
      
    public def get(index: int):T {
    	  return xRead(index);
   }



    public def set(index:int, x:T) {
  
    	changed = true;
        lock.lock();
        this.xWrite(index) = op(this.xWrite(index), x);
        lock.unlock();
    } 
    
    public def setR(index: int, x:T){this.xRead(index)=x;}
    
    public def getW(index: int){return xWrite(index);}
    

    public def move(): Void {
        if (changed)
        	this.xRead = this.xWrite;
     	//this.xWrite = opInit;
    	this.changed = false;
    }

    
}