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

package polyglot.ast;

import polyglot.types.QName;

/**
 * An <code>Import</code> is an immutable representation of a Java
 * <code>import</code> statement.  It consists of the string representing the
 * item being imported and the kind which is either indicating that a class
 * is being imported, or that an entire package is being imported.
 */
public interface Import extends Node 
{
    /** Import kinds: class (e.g., import java.util.Set) or package (e.g.,
     *  import java.util.*).
     *
     * PACKAGE is a bit of a misnomer, since we can import p.C.*, where p.C
     * is a class.  This puts the nested classes of p.C in scope.
     */
    public static enum Kind {
        CLASS("class"),PACKAGE("package");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }               
    }

    public static final Kind CLASS   = Kind.CLASS;
    public static final Kind PACKAGE = Kind.PACKAGE;

    /** Get the name of the class or package to import. */
    QName name();
    /** Set the name of the class or package to import. */
    Import name(QName name);

    /** Get the kind of import. */
    Kind kind();
    /** Set the kind of import. */
    Import kind(Kind kind);
}
