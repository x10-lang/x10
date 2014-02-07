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

import java.util.List;

/** A resolver that also checks if the type found is accessible from the given class. */
public interface AccessControlResolver extends Resolver {
    /**
     * Find a type by name, checking if the object is accessible from the accessor class.
     * A null accessor indicates no access check should be performed.
     */
    public List<Type> find(Matcher<Type> matcher, Context context) throws SemanticException;
}
