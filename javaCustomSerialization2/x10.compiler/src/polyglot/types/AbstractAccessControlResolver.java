/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;

/**
 * Abstract base class of concrete access control resolvers. Implements the
 * Resolver interface by delegating to the access control resolver interface,
 * but performs no access control checks.
 */
public abstract class AbstractAccessControlResolver implements AccessControlResolver {
    protected TypeSystem ts;
    
    public AbstractAccessControlResolver(TypeSystem ts) {
        this.ts = ts;
    }
    
    public final List<Type> find(Matcher<Type> matcher) throws SemanticException {
        return find(matcher, null);
    }
}
