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

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The top of the type hierarchy.
 * Implemented by all classes and structs.
 * 
 * Restriction: The types in Any cannot use "here". (In the current implementation,
 * using "here" in a type, e.g. def at(p:Object!):boolean, would cause an infinite
 * recursion. See PlaceChecker.pushHereTerm.)
 *
 * @author vj 9/1/2010 4:01:06 PM
 */
@NativeRep("java", "x10.core.Global", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Global>", "x10::lang::Global", null)
public interface Global(
    /**
     * The home location of this entity.
     * This will be 'here' for non-object entities.
     */
    @Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
    @Native("c++", "x10::lang::Place_methods::place(x10aux::get_location(#0))")
    home: Place
) {

    /**
     * Return the home location of this entity.
     * This will be 'here' for non-object entities.
     * @return the home location of this entity.
     
    @Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
    @Native("c++", "x10::lang::Place_methods::place(x10aux::get_location(#0))")
    property def home():Place;
*/
    /**
     * Return true if this entity is in the same home location as the given object.
     *
     * @param r The given object
     * @return true if the home location of this entity is the same as the home location of r.
     */
    @Native("java", "x10.core.Ref.at((java.lang.Object)(#0), #1)")
    @Native("c++", "(x10aux::get_location(#0) == (#1)->location)")
    property safe def at(r:GlobalObject):Boolean;

    /**
     * Return true if the home location of this entity is the given place.
     *
     * @param p The given place
     * @return true if the home location of this entity is p.
     */
    @Native("java", "x10.core.Ref.at((java.lang.Object)(#0), #1.id)")
    @Native("c++", "(x10aux::get_location(#0) == (#1)->FMGL(id))")
    property safe def at(p:Place):Boolean;

}

// vim:shiftwidth=4:tabstop=4:expandtab
