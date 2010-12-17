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



public class ClockedOpLessRail[T] extends ClockedRailBase[T] implements ClockableVar  {

  
    var xRead: Rail[T]!;
    var xWrite: Rail[T]!;
    val opInit:T;
    val changed:Rail[AtomicInteger]!;
    val length:int;
    
    
    

    public def this (length: int, c: Clock!, opInitial:T) {
	super(length);
	this.length = length; 
    	c.addClockedVar(this); 
	changed = Rail.make[AtomicInteger] (length, (i:int) => new AtomicInteger(0));
    	xRead = Rail.make[T] (length, (i:int) => opInitial);
    	xWrite = Rail.make[T] (length, (i:int) => opInitial);
     	opInit = opInitial;
     }
    
    public def this(length: int, c:Clock!, opInitial:T, init: (int) => T)
     {
	super(length);
	this.length = length; 
    	val clk = c as Clock!; 
	changed = Rail.make[AtomicInteger] (length, (i:int) => new AtomicInteger(0));
    	xRead = Rail.make[T] (length, init);
    	xWrite = Rail.make[T] (length, (i: int) => opInitial);

        c.addClockedVar(this); 
        opInit = opInitial;
        //xWrite = opInitial; 
      }
      
    public @Inline @Header def getClocked(index: int):T {
	  //Console.OUT.println("Reading" + this.xRead(index));
    	  return xRead(index);
   }



    public @Inline @Header def setClocked(index:int, x:T) {
    	(changed(index) as AtomicInteger!).incrementAndGet();
        this.xWrite(index) = x;
 	//Console.OUT.println("Writing" + this.xWrite(index)); 
    } 
    
    public def setR(index: int, x:T){this.xRead(index)=x;}
    
    public def getW(index: int){return xWrite(index);}
    

    public @Inline @Header def move(): Void {
	var i: int = 0;
	for(; i < length; i++) {
        	if ((changed(i) as AtomicInteger!).get() == 1) {
			this.xRead(i) = this.xWrite(i);
		}
    		(changed(i) as AtomicInteger!).set(0);
    
	}
    }

    
}
