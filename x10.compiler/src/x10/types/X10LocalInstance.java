/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;

/**
 * @author vj
 *
 */
public interface X10LocalInstance extends LocalInstance, TypeObject, X10Use<X10LocalDef> {
    /** Type of the local with self==FI. */
    Type rightType();

    public X10LocalInstance error(SemanticException e);

    public X10LocalInstance name(Name name);
    public X10LocalInstance type(Type t);
}
