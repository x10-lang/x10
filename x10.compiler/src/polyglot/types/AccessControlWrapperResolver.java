/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * AccessControlWrapperResolver.java
 * 
 * Author: nystrom
 * Creation date: Oct 24, 2005
 */
package polyglot.types;

import java.util.List;

/** A Resolver that wraps an AccessControlResolver. */
public class AccessControlWrapperResolver implements Resolver {
    protected AccessControlResolver inner;
    protected Context context;
    
    public AccessControlWrapperResolver(AccessControlResolver inner, Context context) {
        this.inner = inner;
        this.context = context;
    }
    
    public List<Type> find(Matcher<Type> matcher) throws SemanticException {
        return inner.find(matcher, context);
    }
}
