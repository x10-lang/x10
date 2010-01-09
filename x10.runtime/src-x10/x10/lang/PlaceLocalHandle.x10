/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeClass;

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
 * the Handle may be mapped to distinct objects.  For example (assuming >1 Place):
 * <verbatim>
 *   val plh:PlaceLocalHandle[T] = ....
 *   val obj:T = plh.get();
 *   at (here.next()) Console.out.println(plh.get() == obj);
 * </verbatim>
 * may print either true or false depending on how the application is
 * using the particular PlaceLocalHandle (mapping the same object at
 * multiple places or mapping distinct object at each place).</p>
 */
@NativeClass("c++", "x10.lang", "PlaceLocalHandle_Impl")
@NativeClass("java", "x10.runtime.impl.java", "PlaceLocalHandle")
public final struct PlaceLocalHandle[T]{T <: Object} {

    public native def this();

    /**
     * @return the object mapped to the handle at the current place
     */
    public native safe def apply():T!;

    // TODO: make everyone use apply() instead
    public safe def get():T! = apply();

    // Only to be used by create methods in PlaceLocalStorage
    public native def set(newVal:T!):void;

    // Only to be used by create methods in PlaceLocalStorage
    static def createHandle[T]():PlaceLocalHandle[T] = PlaceLocalHandle[T]();

    public native safe def hashCode():int;

    public global safe native def toString():String;

    // TODO: fix guard and cast
    public def copyTo[U](dst:Place, len:Int, notifier:()=>Void)/* {T<:Rail[U]} */:Void {
        x10.lang.System.copyTo[U](this as Any as PlaceLocalHandle[Rail[U]], dst, len, notifier);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
