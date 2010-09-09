/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * AccessControlledResolver.java
 * 
 * Author: nystrom
 * Creation date: Oct 24, 2005
 */
package polyglot.types;

/** A resolver that also checks if the type found is accessible from the given class. */
public interface AccessControlResolver extends Resolver {
    /**
     * Find a type object by name, checking if the object is accessible from the accessor class.
     * A null accessor indicates no access check should be performed.
     */
    public Named find(Matcher<Named> matcher, Context context) throws SemanticException;
}
