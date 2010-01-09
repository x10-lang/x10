/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.NativeClass;

/**
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

    // Only to be used by make method and Runtime class
    native def this();

    /**
     * @return the object mapped to the handle at the current place
     */
    public safe native def apply():T!;

    // Only to be used by make method and Runtime class
    native def set(newVal:T!):Void;

    public safe native def hashCode():Int;

    public safe native def toString():String;

    // TODO: fix guard and cast
    public def copyTo[U](dst:Place, len:Int, notifier:()=>Void)/* {T<:Rail[U]} */:Void {
        x10.lang.System.copyTo[U](this as Any as PlaceLocalHandle[Rail[U]], dst, len, notifier);
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument distribution.  The local object will be initialized
     * by evaluating init at each place.  When this method returns, the local objects
     * will be initialized and available via the returned PlaceLocalHandle instance
     * at every place in the distribution.
     *
     * @param dist A distribution specifiying the places where local objects should be created.
     * @param init the initialization closure used to create the local object.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def make[T](dist:Dist, init:()=>T!){T <: Object}:PlaceLocalHandle[T] {
        val handle = at(Place.FIRST_PLACE) PlaceLocalHandle[T]();
        finish for (p in dist.places()) {
            async (p) handle.set(init());
        }
        return handle;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
