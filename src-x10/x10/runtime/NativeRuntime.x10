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
   * May be implemented synchronously or asynchronously.
   * Body cannot spawn activities, use clocks, or raise exceptions.
   */
  @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
  public static def runAt(id:Int, body:()=>Void):Void { body(); }

  /**
   * Java: run body synchronously at place(id) in the same node as the current place.
   * C++: run body. (no need for a native implementation)
   */
  @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
  public static def runAtLocal(id:Int, body:()=>Void):Void { body(); }

  /**
   * Return true if place(id) is in the current node.
   */
   @Native("java", "x10.runtime.impl.java.Runtime.local(#1)")
   public static def local(id:Int):Boolean = true;
}
