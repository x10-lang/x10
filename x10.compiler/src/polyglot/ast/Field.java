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

package polyglot.ast;

import polyglot.types.FieldInstance;

/**
 * A <code>Field</code> is an immutable representation of a Java field
 * access.  It consists of field name and may also have either a 
 * <code>Type</code> or an <code>Expr</code> containing the field being 
 * accessed.
 */
public interface Field extends NamedVariable
{
    /**
     * Get the type object for the field.  This field may not be valid until
     * after type checking.
     */
    FieldInstance fieldInstance();

    /** Set the type object for the field. */
    Field fieldInstance(FieldInstance fi);

    /**
     * Get the field's container object or type.  May be null before
     * disambiguation.
     */
    Receiver target();

    /** Set the field's container object or type. */
    Field target(Receiver target);

    /**
     * Returns whether the target of this field is implicit, that is if the
     * target is either "this" or a classname, and the source code did not
     * explicitly provide a target. 
     */
    boolean isTargetImplicit();
    
    /** 
     * Set whether the target of the field is implicit.
     */
    Field targetImplicit(boolean implicit);
    
    /** Get the field's name. */
    Id name();
    /** Set the field's name. */
    Field name(Id name);
}
