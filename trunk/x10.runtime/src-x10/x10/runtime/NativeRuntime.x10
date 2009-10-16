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

  @Native("java", "System.out.println(#1)")
  @Native("c++", "x10aux::system_utils::println((#1)->toString()->c_str())")
  public native static def println(o:Object) : Void;

  /**
   * Set system exit code
   */
  @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
  @Native("c++", "(x10aux::exitCode = (#1))")
  public static def setExitCode(code: int): void {}

  // Configuration options

  @Native("java", "x10.runtime.impl.java.Runtime.PLACE_CHECKS")
  @Native("c++", "(x10_boolean) false")
  public const PLACE_CHECKS = true;

  @Native("java", "x10.runtime.impl.java.Runtime.NO_STEALS")
  @Native("c++", "x10aux::no_steals()")
  public const NO_STEALS = false;

  @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
  @Native("c++", "x10aux::num_places")
  public const MAX_PLACES = 4;

  @Native("java", "x10.runtime.impl.java.Runtime.INIT_THREADS")
  @Native("c++", "x10aux::num_threads()")
  public const INIT_THREADS = 1;
    
  @Native("java", "x10.runtime.impl.java.Runtime.STATIC_THREADS")
  @Native("c++", "x10aux::static_threads()")
  public const STATIC_THREADS = false;

  /**
   * Run body at place(id).
   * May be implemented synchronously or asynchronously.
   * Body cannot spawn activities, use clocks, or raise exceptions.
   */
  @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
  @Native("c++", "x10aux::run_at(#1, #2)")
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
  public static def local(id:Int):Boolean = id == here.id;

  /**
   * Process one incoming message if any (non-blocking).
   */
  @Native("c++", "x10aux::event_probe()")
  public static def event_probe():Void {}

  /** Accessors for native performance counters
   */
  @Native("c++","x10aux::asyncs_sent")
  static def getAsyncsSent() = 0 as Long;

  @Native("c++","x10aux::asyncs_sent = #1")
  static def setAsyncsSent(v:Long) { }

  @Native("c++","x10aux::asyncs_received")
  static def getAsyncsReceived() = 0 as Long;

  @Native("c++","x10aux::asyncs_received = #1")
  static def setAsyncsReceived(v:Long) { }

  @Native("c++","x10aux::serialized_bytes")
  static def getSerializedBytes() = 0 as Long;

  @Native("c++","x10aux::serialized_bytes = #1")
  static def setSerializedBytes(v:Long) { }

  @Native("c++","x10aux::deserialized_bytes")
  static def getDeserializedBytes() = 0 as Long;

  @Native("c++","x10aux::deserialized_bytes = #1")
  static def setDeserializedBytes(v:Long) { }

  @Native("c++", "x10aux::dealloc(#4.operator->())")
  static def dealloc[T] (o:T) { }

}
