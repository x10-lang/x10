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
@NativeRep("java", "x10.runtime.impl.java.Deque", null, null)
@NativeRep("c++", "x10aux::ref<x10::runtime::Deque>", "x10::runtime::Deque", null)
public class Deque {
  @Native("java", "#0.getQueueSize()")
  @Native("c++", "(#0)->getQueueSize()")
  public native def size():Int;

  @Native("java", "((x10.runtime.Activity) #0.popTask())")
  @Native("c++", "(#0)->popTask()")
  public native def poll():Activity;

  @Native("java", "#0.pushTask(#1)")
  @Native("c++", "(#0)->pushTask(#1)")
  public native def push(t:Activity):Void;

  @Native("java", "((x10.runtime.Activity) #0.deqTask())")
  @Native("c++", "(#0)->deqTask()")
  public native def steal():Activity;
}
