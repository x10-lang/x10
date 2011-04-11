/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;


/**
 * A <code>Resolver</code> is responsible for looking up types and
 * packages by name.
 */
public interface Resolver {
    /**
     * Find a Named type object, usually a package or a class.
     */
    public List<Type> find(Matcher<Type> matcher) throws SemanticException;
}
