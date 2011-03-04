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

import x10.compiler.NativeClass;
import x10.util.Pair;

/**
 * The primary operation on a PlaceLocalHandle is to use it to access an object
 * on the current place.  If the current place is not part of the distribution
 * over which the PlaceLocalHandle is defined, a BadPlaceException will be thrown.</p>
 *
 * A key concept for correct usage of PlaceLocalHandles is that in different places,
 * the Handle may be mapped to distinct objects.  For example (assuming >1 Place):
 * <pre>
 *   val plh:PlaceLocalHandle[T] = ....;
 *   val obj:T = plh();
 *   at (here.next()) Console.out.println(plh() == obj);
 * </pre>
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

    /**
     * Copies the contents of a Rail stored in the given place-local handle in the
     * current place to the Rail stored in the same handle at a given place.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param handle the place-local handle that references the source and destination Rails.
     * @param dst_place the location of the destination Rail.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    // TODO: fix guard and cast -- should be an existential, i.e. exists U such
    // that T<:Rail[U] then no need for U as an explicit type param and the
    // cast should be implicit because of the knowledge that T <: Rail[U]
    // FURTHERMORE: the constraint on explicit U (commented out) is not used by the code
    // below, the cast is still required for the code to be accepted by the type system.
    public def copyTo[U](dst:Place, len:Int, notifier:()=>Void) /*{T<:Rail[U]}*/ :Void {
        val handle = this as Any as PlaceLocalHandle[Rail[U]];
        val finder = ()=>Pair[Rail[U],Int](handle(), 0);
        handle().copyTo(0, dst, finder, len, notifier);
        Runtime.dealloc(finder);
        Runtime.dealloc(notifier);
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
