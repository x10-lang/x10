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

/**
 * A <code>Qualifier</code> can be used to qualify a type: it can be either
 * a package or a named class type.
 */
public interface Qualifier extends TypeObject
{
    /**
     * Return true if the qualifier is a package.
     */
    boolean isPackage();

    /**
     * Cast the qualifier to a package, or return null.
     * This method will probably be deprecated.
     */
    Package toPackage();

    /**
     * Return true if the qualifier is a type.
     */
    boolean isType();

    /**
     * Cast the qualifier to a type, or return null.
     * This method will probably be deprecated.
     */
    Type toType();
}
