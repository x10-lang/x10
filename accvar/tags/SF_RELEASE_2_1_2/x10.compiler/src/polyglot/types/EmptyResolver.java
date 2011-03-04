/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;


/**
 * An <code>EmptyResolver</code> is a resolver that always fails.
 */
public class EmptyResolver implements Resolver {
    protected String kind;

    public EmptyResolver() {
        this("Type or package");
    }

    public EmptyResolver(String kind) {
        this.kind = kind;
    }

    /**
     * Find a type object by name.
     */
    public List<Type> find(Matcher<Type> matcher) throws SemanticException {
        throw new SemanticException((kind != null ? (kind + " ") : "") +
                                    matcher.signature() + " not found.");
    }
}
