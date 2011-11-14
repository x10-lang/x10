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

import x10.compiler.Pragma;
import x10.compiler.NativeClass;

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
    public native operator this():T;

    // Only to be used by make method and Runtime class
    native def set(newVal:T):void;

    public native def hashCode():Int;

    public native def toString():String;

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument distribution.  The local object will be initialized
     * by evaluating init at each place.  When this method returns, the local objects
     * will be initialized and available via the returned PlaceLocalHandle instance
     * at every place in the distribution.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init the initialization closure used to create the local object.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def make[T](dist:Dist, init:()=>T){T <: Object}:PlaceLocalHandle[T] {
        return make(dist.places(), init);
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument distribution.  For each place in the
     * argument distribution, the local_init closure will be evaluated to 
     * yield a value of type U.  This value will then be serialized to the
     * target place and passed as an argument to the init closure. 
     * When this method returns, the local objects will be initialized and available 
     * via the returned PlaceLocalHandle instance at every place in the distribution.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init_here a closure to compute the local portion of the initialization (evaluated in the current place)
     * @param init_there a closure to be evaluated in each place to create the local objects.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def make[T,U](dist:Dist, init_here:(Place)=>U, init_there:(U)=>T){T <: Object}:PlaceLocalHandle[T] {
        return make(dist.places(), init_here, init_there);
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument PlaceGroup.  The local object will be initialized
     * by evaluating init at each place.  When this method returns, the local objects
     * will be initialized and available via the returned PlaceLocalHandle instance
     * at every place in the PlaceGroup.
     *
     * @param pg a PlaceGroup specifiying the places where local objects should be created.
     * @param init the initialization closure used to create the local object.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def make[T](pg:PlaceGroup, init:()=>T){T <: Object}:PlaceLocalHandle[T] {
        val handle = at(Place.FIRST_PLACE) PlaceLocalHandle[T]();
        finish for (p in pg) {
            at (p) async handle.set(init());
        }
        return handle;
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument PlaceGroup.  For each place in the
     * argument PlaceGroup, the local_init closure will be evaluated in the 
     * current place to yield a value of type U.  This value will then be serialized 
     * to the target place and passed as an argument to the init closure. 
     * When this method returns, the local objects will be initialized and available 
     * via the returned PlaceLocalHandle instance at every place in the distribution.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init_here a closure to compute the local portion of the initialization (evaluated in the current place)
     * @param init_there a closure to be evaluated in each place to create the local objects.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def make[T,U](pg:PlaceGroup, init_here:(Place)=>U, init_there:(U)=>T){T <: Object}:PlaceLocalHandle[T] {
        val handle = at(Place.FIRST_PLACE) PlaceLocalHandle[T]();
        finish for (p in pg) {
            val v:U = init_here(p);
            at (p) async handle.set(init_there(v));
        }
        return handle;
    }

    /*
     * Same API, but replicated for flat asyncs (optimized finish implementation
     */

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument distribution.  The local object will be initialized
     * by evaluating init at each place.  When this method returns, the local objects
     * will be initialized and available via the returned PlaceLocalHandle instance
     * at every place in the distribution.
     * 
     * Requires an initialization closure that does not change place asynchronously.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init the initialization closure used to create the local object.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def makeFlat[T](dist:Dist, init:()=>T){T <: Object}:PlaceLocalHandle[T] {
        return makeFlat(dist.places(), init);
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument distribution.  For each place in the
     * argument distribution, the local_init closure will be evaluated to 
     * yield a value of type U.  This value will then be serialized to the
     * target place and passed as an argument to the init closure. 
     * When this method returns, the local objects will be initialized and available 
     * via the returned PlaceLocalHandle instance at every place in the distribution.
     *
     * Requires an initialization closure that does not change place asynchronously.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init_here a closure to compute the local portion of the initialization (evaluated in the current place)
     * @param init_there a closure to be evaluated in each place to create the local objects.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def makeFlat[T,U](dist:Dist, init_here:(Place)=>U, init_there:(U)=>T){T <: Object}:PlaceLocalHandle[T] {
        return makeFlat(dist.places(), init_here, init_there);
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument PlaceGroup.  The local object will be initialized
     * by evaluating init at each place.  When this method returns, the local objects
     * will be initialized and available via the returned PlaceLocalHandle instance
     * at every place in the PlaceGroup.
     *
     * Requires an initialization closure that does not change place asynchronously.
     *
     * @param pg a PlaceGroup specifiying the places where local objects should be created.
     * @param init the initialization closure used to create the local object.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def makeFlat[T](pg:PlaceGroup, init:()=>T){T <: Object}:PlaceLocalHandle[T] {
        val handle = at(Place.FIRST_PLACE) PlaceLocalHandle[T]();
        @Pragma(Pragma.FINISH_SPMD) finish for (p in pg) {
            at (p) async handle.set(init());
        }
        return handle;
    }

    /**
     * Create a distributed object with local state of type T
     * at each place in the argument PlaceGroup.  For each place in the
     * argument PlaceGroup, the local_init closure will be evaluated in the 
     * current place to yield a value of type U.  This value will then be serialized 
     * to the target place and passed as an argument to the init closure. 
     * When this method returns, the local objects will be initialized and available 
     * via the returned PlaceLocalHandle instance at every place in the distribution.
     *
     * Requires an initialization closure that does not change place asynchronously.
     *
     * @param dist a distribution specifiying the places where local objects should be created.
     * @param init_here a closure to compute the local portion of the initialization (evaluated in the current place)
     * @param init_there a closure to be evaluated in each place to create the local objects.
     * @return a PlaceLocalHandle that can be used to access the local objects.
     */
    public static def makeFlat[T,U](pg:PlaceGroup, init_here:(Place)=>U, init_there:(U)=>T){T <: Object}:PlaceLocalHandle[T] {
        val handle = at(Place.FIRST_PLACE) PlaceLocalHandle[T]();
        @Pragma(Pragma.FINISH_SPMD) finish for (p in pg) {
            val v:U = init_here(p);
            at (p) async handle.set(init_there(v));
        }
        return handle;
    }
}
