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
