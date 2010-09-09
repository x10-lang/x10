/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.Enum;

/**
 * A <code>PrimitiveType</code> represents a type which may not be directly 
 * coerced to java.lang.Object (under the standard Java type system).    
 * <p>
 * This class should never be instantiated directly. Instead, you should
 * use the <code>TypeSystem.get*</code> methods.
 */
public interface PrimitiveType extends ValueType, Named
{
    /**
     * A string representing the type used to box this primitive.
     */
    String wrapperTypeString(TypeSystem ts);
}
