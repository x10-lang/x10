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

package jgfutil;

import x10.io.Console;

/**
  * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0)
  *
  * @author Vivek Sarkar (vsarkar@us.ibm.com)
  *
  * Porting issues identified:
  * 1) Extend x10.lang.Object
  */
  
public class JGFTimer {

    public var name: String; 
    public var opname: String; 
    public var time: double; 
    public var opcount: double; 
    public var calls: long; 
  public var size: int = -1;
  
  private var start_time: long;
  private var on: boolean; 

  public def this(var name: String, var opname: String): JGFTimer = {
    this.name = name;
    this.opname = opname;
    reset(); 
  }

  public def this(var name: String, var opname: String, var size: int): JGFTimer = {
    this.name = name;
    this.opname = opname;
    this.size = size;
    reset();
  }

  public def this(var name: String): JGFTimer = {
    this(name,""); 
  }



  public def start(): void = {
    if (on) Console.OUT.println("Warning timer " + name + " was already turned on");
    on = true; 
    start_time = System.currentTimeMillis();
  }


  public def stop(): void = {
    time += ((System.currentTimeMillis()-start_time) / 1000.) as Double;
    if (!on) Console.OUT.println("Warning timer " + name + " wasn't turned on");
    calls++;
    on = false;  
  }

  public def addops(var count: double): void = {
    opcount += count;
  } 

  final public def reset(): void = {
    time = 0.0; 
    calls = 0; 
    opcount = 0; 
    on = false;
  }

  public def perf(): double = {
    return opcount / time; 
  }

  public def longprint(): void = {
      Console.OUT.println("Timer            Calls         Time(s)       Performance("+opname+"/s)");   
     Console.OUT.println(name + "           " + calls +    "           "  +  time + "        " + this.perf());
  }

  public def print(): void = {
    if (opname.equals("")) {
      Console.OUT.println(name + "   " + time + " (s)");
    }
    else {

      switch(size) {
      case 0:
      Console.OUT.println(name + ":SizeA" + "\t" + time + " (s) \t "
			  + (this.perf() as Float) + "\t" 
			  + " ("+opname+"/s)");
      break;
      case 1:
      Console.OUT.println(name + ":SizeB" + "\t" + time + " (s) \t " 
			  + (this.perf() as Float) + "\t" 
			  + " ("+opname+"/s)");
      break;
      case 2:
      Console.OUT.println(name + ":SizeC" + "\t" + time + " (s) \t " 
			  + (this.perf() as Float) + "\t" 
			  + " ("+opname+"/s)");
      break;
      default:
      Console.OUT.println(name + "\t" + time + " (s) \t " 
			  + (this.perf() as Float) + "\t" 
			  + " ("+opname+"/s)");
      break;
      }

    }
  }


  public def printperf(): void = {

     var name: String;
     name = this.name; 

     // pad name to 40 characters
     while ( name.length() < 40 ) name = name + " "; 
     
     Console.OUT.println(name + "\t" + (this.perf() as Float) + "\t"
			+ " ("+opname+"/s)");  
  }

}
