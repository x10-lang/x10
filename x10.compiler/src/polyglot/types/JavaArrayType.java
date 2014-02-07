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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import x10.types.MethodInstance;

/**
 * An <code>ArrayType</code> represents an array of other types.
 */
public interface JavaArrayType extends ObjectType 
{
    /**
     * Base type of the array.
     */
    Ref<? extends Type> theBaseType();
    Type base();

    /**
     * Set the base type of the array, returning a new type.
     */
    JavaArrayType base(Ref<? extends Type> base);

    /**
     * The ultimate base of the array.  Guaranteed not to be an array type.
     */
    Type ultimateBase();

    /**
     * The array's length field.
     */
    FieldInstance lengthField();

    /**
     * The array's clone() method.
     */
    MethodInstance cloneMethod();

    /**
     * Return the number of dimensions in this array type.
     * e.g., for A[], return 1; for A[][], return 2, etc.
     */
    int dims();
}
