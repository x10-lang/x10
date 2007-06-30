/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
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
    
import java.util.*;

  public safe class JGFInstrumentor extends x10.lang.Object {

  private static final Hashtable timers;
  private static final Hashtable data; 

  static {
    timers = new Hashtable();
    data = new Hashtable(); 
  }

  public static atomic void addTimer (String name){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			 " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name));
    }
  }
    
  public static atomic  void addTimer(String name, String opname){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			 " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name,opname));
    }
    
  }

  public static atomic void addTimer (String name, String opname, int size){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name +
                         " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name,opname,size));
    }

  }

  public static atomic void startTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).start();
    }
    else {
      System.out.println("JGFInstrumentor.startTimer: failed -  timer " + name + 
			 " does not exist");
    }

  }

  public static atomic void stopTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).stop();
    }
    else {
      System.out.println("JGFInstrumentor.stopTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }

  public static atomic void addOpsToTimer(String name, double count){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).addops(count);
    }
    else {
      System.out.println("JGFInstrumentor.addOpsToTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }  

  public static atomic double readTimer(String name){
    double time; 
    if (timers.containsKey(name)) {
      time = ((JGFTimer) timers.get(name)).time;
    }
    else {
      System.out.println("JGFInstrumentor.readTimer: failed -  timer " + name + 
			 " does not exist");
       time = 0.0; 
    }
    return time; 
  }  

  public static atomic void resetTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).reset();
    }
    else {
      System.out.println("JGFInstrumentor.resetTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void printTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).print();
    }
    else {
      System.out.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void printperfTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).printperf();
    }
    else {
      System.out.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void storeData(String name, x10.lang.Object obj){
    data.put(name,obj); 
  }

  public static atomic void retrieveData(String name, x10.lang.Object obj){
      obj = (x10.lang.Object) data.get(name); 
  }
  
  public static atomic safe void printHeader(int section, int size) {
  	printHeader(section, size, 1);
  }

  public static atomic safe void printHeader(int section, int size,int nthreads) {

    String header, base;

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

    System.out.println(header);

    if (nthreads == 1) {
      System.out.println("Executing on " + nthreads + " thread");
    }
    else {
      System.out.println("Executing on " + nthreads + " threads");
    }

    System.out.println("");

  }

}
