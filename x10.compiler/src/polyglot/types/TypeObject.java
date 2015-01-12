/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.io.Serializable;

import polyglot.util.Copy;
import polyglot.util.Position;

/**
 * A <code>TypeObject</code> is a compile-time value created by the type system.
 * It is a static representation of a type that is not necessarily 
 * first-class.  It is similar to a compile-time meta-object.
 */
public interface TypeObject extends Copy, Serializable
{
    /**
     * The object's type system.
     */
    TypeSystem typeSystem();

    /**
     * The object's position, or null.
     */
    Position position();
    
    Position errorPosition();
    
    void equals(Type t);
//    void equals(TypeObject t);
    void equalsImpl(Type t);
    void equalsImpl(Object t);

    /**
     * Return true iff this type object is the same as <code>t</code>.
     * All Polyglot extensions should attempt to maintain pointer
     * equality between equal TypeObjects.  If this cannot be done,
     * extensions can override TypeObject_c.equalsImpl(), and
     * don't forget to override hashCode().
     *
     * @see polyglot.types.TypeObject_c#equalsImpl(TypeObject)
     * @see java.lang.Object#hashCode()
     */
    boolean equalsImpl(TypeObject t);
}
