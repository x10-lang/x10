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

package x10.lang;

import x10.compiler.Global;
import x10.compiler.Native;

/**
 * A struct that adds size information to a GlobalRef[Rail[T]]
 * in order to enable safe DMA operations via Rail.asyncCopy.
 * 
 * The following relationship will always be true, but is not expressible
 * due to limitations of the current implementations of constrained types in X10.
 * <pre>
 * this.size == T.size, where this.rail : GlobalRef[T]
 * </pre>
 */
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
    }
    
    /**
     * Create a GlobalRail using a raw size and remote rail.  This is unsafe
     * since it may be used to violate the (unenforced) constraint that
     * self.size == self.rail().size.  However it is required internally
     * by the CUDA runtime, where it is accesed directly from C++ to
     * bypass the 'private'.
     */
    private def this(size:Long, raw:GlobalRef[Rail[T]{self!=null}]) {
        property(size, raw);
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

}
