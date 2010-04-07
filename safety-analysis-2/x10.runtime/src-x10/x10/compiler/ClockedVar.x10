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



public class ClockedVar[T] implements ClockableVar{
    var xRead:T;
    var xWrite: T;
    val op:(T,T)=>T;

    public def this (c: Clock!, oper: (T,T)=>T!) { c.addClockedVar(this); op = oper;}
    public def this(c:Clock, oper: (T,T)=>T, x:T){val clk = c as Clock!; this.xRead = this.xWrite = x; clk.addClockedVar(this); op = oper;}
    public def get():T { Console.OUT.println("In clocked get"); return xRead;}
    public def set(x:T){
    	atomic if (this.xWrite == null)
    					this.xWrite = x;
    	else
    		  this.xWrite = op(this.xWrite, x);
    } 
    public def setR(x:T){this.xRead=x;}
    public def getW(){return xWrite;}
    public def reset(){}
    public def move(): Void { this.setR( this.getW()); this.reset();}

    
}