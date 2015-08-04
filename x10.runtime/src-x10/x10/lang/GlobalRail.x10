/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.compiler.Global;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

/**
 * A struct that adds size information to a GlobalRef[Rail[T]]
 * in order to enable safe DMA operations via Rail.asyncCopy.
 * 
 * The following relationship will always be true, but is not expressible
 * due to limitations of the current implementations of constrained types in X10.
 * <pre>
 * this.size == this.rail().size
 * </pre>
 */
@NativeCPPInclude("x10/lang/RemoteOps.h")
public final struct GlobalRail[T] (
        /**
         * The size of the remote rail.
         */
        size:Long, 
        /**
         * The GlobalRef to the remote rail.
         */
        rail:GlobalRef[Rail[T]{self!=null}]
) {

    /**
     * The home location of the GlobalRail is equal to rail.home
     */
    public property home():Place = rail.home;

    /**
     * Create a GlobalRail wrapping the local Rail argument.
     * @param a The rail object to make accessible remotely.
     */
    public def this(a:Rail[T]{self!=null}) {
        property(a.size, GlobalRef[Rail[T]{self!=null}](a));
        { @Native("c++", "x10rt_register_mem(a->raw, a->FMGL(size) * sizeof(TPMGL(T)));") {} }
    }
    
    /**
     * Create a GlobalRail using a raw size and remote rail.  This is unsafe
     * since it may be used to violate the (unenforced) constraint that
     * self.size == self.rail().size.  However it is required internally
     * by the CUDA runtime, where it is accesed directly from C++ to
     * bypass the 'private' and is also used by Unsafe.
     */
    def this(size:Long, raw:GlobalRef[Rail[T]{self!=null}]) {
        property(size, raw);
        { @Native("c++", "x10rt_register_mem(raw->__apply()->raw, size * sizeof(TPMGL(T)));") {} }
    }

    /**
     * Return the element of this rail corresponding to the given index.
     * Can only be called where <code>here == rail.home</code>. 
     * 
     * @param index the given index
     * @return the element of this rail corresponding to the given index.
     */
    @Native("cuda", "(#this).raw[#index]")
    public operator this(index:Long) {here==rail.home}:T = this()(index);

    /**
     * Set the element of this rail corresponding to the given index to the given value.
     * Return the new value of the element.
     * Can only  be called where <code>here == rail.home</code>. 
     * 
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @return the new value of the element of this rail corresponding to the given index.
     */
    @Native("cuda", "(#this).raw[#index] = (#v)")
    public operator this(index:Long)=(v:T) {here==rail.home}:T{self==v} = this()(index)=v;

    /**
     * Access the Rail that is encapsulated by this GlobalRail. 
     * Can only  be called where <code>here == rail.home</code>. 
     */
    @Native("cuda", "(#this)")
    public operator this() {here==rail.home} : Rail[T]{self!=null} = rail();

    /*
     * Support for remote update operations (Power775 HFI access).
     *
     * NOTE:  The remote update operations are not thread safe.
     *        They cannot be used simultaneously by multiple X10 activities
     *        in the same Place.  
     *        This constraint is not dynamically checked; concurrent access
     *        will simply result in incorrect operations.
     *        The cause is batching of operations in x10aux::network.h
     */

    // @Native("java", "x10.x10rt.X10RT.remoteAdd__1$u(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteAdd((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteAdd(target:GlobalRail[ULong], idx:Long, v:ULong):void {
        at (target.home) { target(idx) += v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteAdd(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteAdd((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteAdd(target:GlobalRail[Long], idx:Long, v:Long):void {
        at (target.home) { target(idx) += v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteAnd__1$u(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteAnd((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteAnd(target:GlobalRail[ULong], idx:Long, v:ULong):void {
        at (target.home) { target(idx) &= v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteAnd(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteAnd((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteAnd(target:GlobalRail[Long], idx:Long, v:Long):void {
        at (target.home) { target(idx) &= v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteOr__1$u(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteOr((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteOr(target:GlobalRail[ULong], idx:Long, v:ULong):void {
        at (target.home) { target(idx) |= v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteOr(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteOr((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteOr(target:GlobalRail[Long], idx:Long, v:Long):void {
        at (target.home) { target(idx) |= v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteXor__1$u(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteXor((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteXor(target:GlobalRail[ULong], idx:Long, v:ULong):void {
        at (target.home) { target(idx) ^= v; }
    }

    // @Native("java", "x10.x10rt.X10RT.remoteXor(#target, #idx, #v)")
    @Native("c++", "::x10::lang::RemoteOps::remoteXor((#target)->FMGL(rail), #idx ,#v)")
    public static def remoteXor(target:GlobalRail[Long], idx:Long, v:Long):void {
        at (target.home) { target(idx) ^= v; }
    }

    @Native("java", "")
    @Native("c++", "::x10aux::flush_remote_ops()")
    public static native def flushRemoteOps():void;
}
