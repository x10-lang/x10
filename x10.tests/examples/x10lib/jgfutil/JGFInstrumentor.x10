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


/**
  * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0)
  *
  * @author Vivek Sarkar (vsarkar@us.ibm.com)
  *
  * Porting issues identified:
  * 1) Extend x10.lang.Object
  */
    
import x10.util.HashMap;
import x10.io.Console;
public class JGFInstrumentor {

  private const timers = new HashMap[String, JGFTimer]();
  private const data  =  new HashMap[String, Object](); 

    public atomic static def addTimer(name: String) {

	if (timers.containsKey(name)) {
	    Console.OUT.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			       " already exists");
	}
	else {
	    timers.put(name, new JGFTimer(name));
	}
    }
    
    public atomic static def addTimer(name: String, opname: String) {

	if (timers.containsKey(name)) {
	    Console.OUT.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			       " already exists");
	}
	else {
	    timers.put(name, new JGFTimer(name,opname));
	}
    }

  public atomic static def addTimer(name: String, opname: String, size: int) {

    if (timers.containsKey(name)) {
      Console.OUT.println("JGFInstrumentor.addTimer: warning -  timer " + name +
                         " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name,opname,size));
    }

  }

  public atomic static def startTimer(name: String)= {
    if (timers.containsKey(name)) {
      timers.get(name).value.start();
    }
    else {
      Console.OUT.println("JGFInstrumentor.startTimer: failed -  timer " + name + 
			 " does not exist");
    }

  }

  public atomic static def stopTimer(name: String) {
    if (timers.containsKey(name)) {
      timers.get(name).value.stop();
    }
    else {
      Console.OUT.println("JGFInstrumentor.stopTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }

  public atomic static def addOpsToTimer(name: String, count: double) {
    if (timers.containsKey(name)) {
      (timers.get(name)).value.addops(count);
    }
    else {
      Console.OUT.println("JGFInstrumentor.addOpsToTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }  

  public atomic static def readTimer(name: String): Double  {
    var time: double; 
    if (timers.containsKey(name)) {
      time = timers.get(name).value.time;
    }
    else {
      Console.OUT.println("JGFInstrumentor.readTimer: failed -  timer " + name + 
			 " does not exist");
       time = 0.0; 
    }
    return time; 
  }  

  public atomic static def resetTimer(name: String) {
    if (timers.containsKey(name)) {
      timers.get(name).value.reset();
    }
    else {
      Console.OUT.println("JGFInstrumentor.resetTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public atomic static def printTimer(name: String) {
    if (timers.containsKey(name)) {
      timers.get(name).value.print();
    }
    else {
      Console.OUT.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public atomic static def printperfTimer(name: String) {
    if (timers.containsKey(name)) {
      timers.get(name).value.printperf();
    }
    else {
      Console.OUT.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public atomic static def storeData(name: String, obj: Object) {
    data.put(name,obj); 
  }

  public atomic static def retrieveData(name: String, var obj: Object) {
      obj = data.get(name); 
  }
  
  public atomic static def printHeader(section: Int, size: Int) {
  	printHeader(section, size, 1);
  }

  public atomic safe static def printHeader(section: Int, size: Int, nthreads: Int) {

    var header: String;
    var base: String;

    header = "";
    base = "Java Grande Forum Thread Benchmark Suite - Version 1.0 - Section ";

    switch (section) {
    case 1:
      header = base + "1";
      break;
    case 2:
      switch (size) {
      case 0:
        header = base + "2 - Size A";
        break;
      case 1:
        header = base + "2 - Size B";
        break;
      case 2:
        header = base + "2 - Size C";
        break;
      }
      break;
    case 3:
      switch (size) {
      case 0:
        header = base + "3 - Size A";
        break;
      case 1:
        header = base + "3 - Size B";
        break;
      }
      break;
    }

    Console.OUT.println(header);

    if (nthreads == 1) {
      Console.OUT.println("Executing on " + nthreads + " thread");
    }
    else {
      Console.OUT.println("Executing on " + nthreads + " threads");
    }

    Console.OUT.println("");

  }

}
