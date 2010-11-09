/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.util.InternalCompilerError;

/**
 * Representation of X10 flags.
 *
 * @author pcharles
 * @author vj
 *
 */
public class X10Flags extends Flags {
    public X10Flags() {
        super();
    }
    public X10Flags(String name) {
        super(name);
    }
    public static X10Flags toX10Flags(Flags f) {
        return (X10Flags)f;
    }



}
