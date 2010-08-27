/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;


/**
 * A <code>Resolver</code> is responsible for looking up types and
 * packages by name.
 */
public interface Resolver {
    /**
     * Find a Named type object, usually a package or a class.
     */
    public Named find(Matcher<Named> matcher) throws SemanticException;
}
