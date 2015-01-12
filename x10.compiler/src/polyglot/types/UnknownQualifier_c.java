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
 * An unknown type qualifier.  This is used as a place-holder until types
 * are disambiguated.
 */
public class UnknownQualifier_c extends TypeObject_c implements UnknownQualifier
{
    private static final long serialVersionUID = 5900640134201163919L;

    public UnknownQualifier_c(TypeSystem ts) {
        super(ts);
    }

    public boolean isPackage() { return false; }
    public boolean isType() { return false; }

    public Package toPackage() { return null; }
    public Type toType() { return null; }

    public String toString() {
        return "<unknown>";
    }
}
