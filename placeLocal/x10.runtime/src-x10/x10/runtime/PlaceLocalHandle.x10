/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A PlaceLocalHandle is used in conjunction with the PlaceLocalStorage
 * facility to create, name, manage, and destory the place-local storage of 
 * a distributed object. PlaceLocalHandles are created internally by the 
 * PlaceLocalStorage service; they cannot be directly created by application code.</p>
 *
 * The primary operation on a PlaceLocalHandle is to use it to access an object
 * on the current place.  If the current place is not part of the distribution
 * over which the PlaceLocalHandle is defined, a BadPlaceException will be thrown.</p>
 *
 * A key concept for correct usage of PlaceLocalHandles is that in different places,
 * the Handle will be mapped to distinct objects.  For example (assuming >1 Place):
 * <verbatim>
 *   val plh:PlaceLocalHandle[T] = ....
 *   val obj:T = plh.get();
 *   at (here.next()) Console.out.println(plh.get() == obj);
 * </verbatim>
 * will print false.</p>
 */
@NativeRep("c++", "x10aux::ref<x10::runtime::PlaceLocalHandle<#1 > >", "x10::runtime::PlaceLocalHandle<#1 >", null)
@NativeRep("java", "x10.runtime.impl.java.PlaceLocalHandle<#1>", null, null)
public final value PlaceLocalHandle[T] {

  /**
   * @return the object mapped to the handle at the current place
   */
  @Native("c++", "(#0)->get()")
  @Native("java", "#0.get()")
  public native safe def get():T;

  @Native("c++", "(#0)->hashCode()")
  @Native("java", "#0.hashCode()")
  public native safe def hashCode():int;

  // Only to be used by create methods in PlaceLocalStorage
  @Native("c++", "(#0)->set(#1)")
  @Native("java", "#0.set(#1)")
  native def set(newVal:T):void;

  // Only to be used by create methods in PlaceLocalStorage
  @Native("c++", "x10::runtime::PlaceLocalHandle<#1 >::createHandle()")
  @Native("java", "x10.runtime.impl.java.PlaceLocalHandle.createHandle()")
  static native def createHandle[T]():PlaceLocalHandle[T];

}
