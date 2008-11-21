/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Interface with native runtime
 * @author tardieu
 */
//@NativeRep("java", "x10.runtime.impl.java.Runtime", null, null)
public value NativeRuntime {

  /**
   * Set system exit code
   */
  @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
  public static def setExitCode(code: int): void {}

  // Configuration options

  @Native("java", "x10.runtime.impl.java.Runtime.PLACE_CHECKS")
  public const PLACE_CHECKS = true;

  @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
  public const MAX_PLACES = 4;

  @Native("java", "x10.runtime.impl.java.Runtime.INIT_THREADS")
  public const INIT_THREADS = 3;
    
  /**
   * Run body at place(id).
   * Wait for body to terminate.
   * Must use current thread if in the same node!!!
   */
  @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
  public static def runAt(id:Int, body:()=>Void):Void { body(); }

  /**
   * Return true if place(id) is in the current node.
   */
   @Native("java", "x10.runtime.impl.java.Runtime.local(#1)")
   public static def local(id:Int):Boolean = true;
}
