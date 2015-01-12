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


import polyglot.util.CodeWriter;

/**
 * An <code>Package</code> represents a Java package.
 */
public interface Package extends Qualifier, Named, Def
{
    /**
     * The package's outer package.
     */
    Ref<? extends Package> prefix();

    /**
     * Return a string that is the translation of this package.
     * @param c A resolver in which to look up the package.
     */
    String translate(Resolver c);
    
    /** Return true if this package is equivalent to <code>p</code>. */
    boolean packageEquals(Package p);

    /** A resolver to access member packages and classes of the package. */
    Resolver resolver();

    /** Pretty-print this package name to w. */
    void print(CodeWriter w);
}
