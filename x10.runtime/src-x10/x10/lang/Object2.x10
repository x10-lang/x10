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
import x10.compiler.NonEscaping;

/**
 * The base class for all reference classes.
 */
// Fix for XTENLANG-1916
@NativeRep("java", "x10.core.RefI", null, "x10.rtt.Types.OBJECT")
@NativeRep("c++", "x10::lang::Object*", "x10::lang::Object", null)
public class Object2 {

    /**
     * Default constructor.
     */
    // (for java) Note: new x10.lang.Object() returns x10.core.Ref instead of java.lang.Object
    // XTENLANG-3063
	// @Native("java", "new x10.core.Ref((java.lang.System[]) null).$init()")
    @Native("java", "new x10.core.Ref((java.lang.System[]) null).x10$lang$Object$$init$S()")
    public native def this();

    /**
     * Return true if the given entity is an Object, and is == to this. The method should 
     * be overridden in user classes that wish to define their own notion of equality.
     *
     * <p>  Please see the documentation for x10.lang.Any.equals.
     * @seeAlso x10.lang.Any
     * @param that the given entity
     * @return true if this object is equal to the given entity.
     */
    @Native("java", "#this.equals(#that)")
    @Native("c++", "(#this)->equals(#that)")
    public native def equals(that:Any): Boolean;

    /**
     * Return the default (implementation-defined) hash code of this object. The method
     * should be overridden in user classes that wish to define their own notion of equality
     * so that if two objects are not equal they do not have the same hash code.
     * 
     * <p>  Please see the documentation for x10.lang.Any.hashCode().
     * @seeAlso x10.lang.Any
     * @return the hash code of this object.
     */
    @Native("java", "#this.hashCode()")
    @Native("c++", "(#this)->hashCode()")
    public native def hashCode() : Int;

    /**
     * Return the default string representation of this object.
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the string representation
     * has to be based on only information available at the current place, 
     * or the implementation has to throw an exception if it cannot perform the toString
     * operation with the avaiable information.
     *
     * @return a string representation of this object.
     */
    @Native("java", "#this.toString()")
    @Native("c++", "(#this)->toString()")
    public native def toString() : String;

    /**
     * Return a string representation of the run-time type of this object.
     *
     * @return a string representation of the run-time type of this object.
     */
    @Native("java", "x10.rtt.Types.typeName(#this)")
    @Native("c++", "x10aux::type_name(#this)")
    @NonEscaping
    public native final def typeName():String;
}
