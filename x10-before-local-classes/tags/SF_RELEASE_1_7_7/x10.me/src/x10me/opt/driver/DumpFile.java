/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.driver;

import java.io.*;

import x10me.types.Type;

/**
 * This class manages the dump files for the compiler.
 * 
 * The current implementation builds a series of dump files for each 
 * class being compiled.   These classes are produced in the directory 
 * that the compilation occurs in (this would be easy to change).
 * 
 * The file names are of the form:
 * 
 * <className> + "." + <NNN> + "." + <passName> + ".x10.debug" 
 * 
 * The NNN are three digit numbers that start at 100 and are incremented 
 * by one for each pass.  This makes the natural sorting of the file names
 * match the order that the passes are performed.   It also mean that if 
 * a single pass is performed many times during the compilation of a single 
 * method, each instance will have its own dump file.  However, each dump file
 * will contain the dumps of that pass for all of the methods of that 
 * single class. 
 */
public final class DumpFile {
  
  public PrintStream current;
  private PrintStream privateCurrent;

  /** 
   * The class currently being compiled.
   */
  private Type type = null;

  /**
   * The name of the class currently being compiled. 
   */
  String className;
  
  /** 
   * Name of the pass currently being executed.
   */
  private String passName = null;
  
  /* 
   * Valid pass numbers start with 100.   This means that 
   * the dump file will always appear in sorted order (at least 
   * as long as there are less 899 passes.
   */
  private static final int UNSET = 0;
  private static final int FIRST = 100;
  private static final String SUFFIX = ".x10.debug"; 
  
  /**
   * The current pass number.
   */
  private int passNumber;
  /** 
   * Set the name of the class currently being compiled.
   * @param name of the class currently being compiled.
   */
  public DumpFile (Type type) {
    this.type = type;
    className = this.type.getName();
    passNumber = UNSET;

    /* 
     * Delete all of the old dump files associated with this class.
     */
    File currentDir = new File (".");
    String [] oldDumpFiles = currentDir.list (new DebugFileFilter (className));
    for (String f : oldDumpFiles) {
       new File (f).delete(); 
    }  
  }
  
  /** 
   * A FilenameFilter to find the old dump files.
   */
  static class DebugFileFilter implements FilenameFilter {
    String prefix;
    DebugFileFilter (String name) {
      prefix = name + ".";
    }
    
  //  @Override
    public boolean accept (File dir, String name) {
      return name.startsWith(prefix) && name.endsWith(SUFFIX); 
    }
  }
  
  /** 
   * Set the name of the method currently being compiled.
   * @param name of the method currently being compiled.
   */
  public void setCompiledMethod (String name) {
    /*
     * The methodName is currently unused.  But could be used if 
     * we wished to produce many more smaller dump files for 
     * a compilation.
     *
     * Reset the pass number whenever we start compiling 
     * another method.  Since the files are always opened 
     * in append mode, the info for each method will in a class 
     * for a pass will appear in the right order.
     */
    passNumber = FIRST;
  }
  
  /** 
   * Set the name of the pass currently being executed.
   * @param name of the pass currently being executed.
   */
  public void setCurrentPass (String name) {
    passName = name;
    passNumber++;

    /*
     * Close the dump file from the previous pass.
     */
    if (privateCurrent != null) {
      privateCurrent.close ();
    }
    
    current = null;
    privateCurrent = null;
    assert passNumber > UNSET;
  }
  
  /**
   * Allow dumping in the current pass by making the print stream visible.
   */
  public void enableDumping () {
    if (privateCurrent == null) {
      String fileName = className + "." + passNumber + "." + passName + SUFFIX;
      try {
        /* 
         * Open the file append, so that we can append each methods output.
         */
        privateCurrent = new PrintStream (new FileOutputStream (fileName, true));
      } catch (java.io.FileNotFoundException v) {}
    }
    
    current = privateCurrent;
  }

  /**
   * Disable dumping in the current pass by hiding the print stream.
   */
  public void disableDumping() {
    current = null;
  }
}