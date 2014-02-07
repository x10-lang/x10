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

import polyglot.frontend.Source;
import x10.types.X10ClassDef;

/**
 * A <code>ParsedClassType</code> represents a class loaded from a source file.
 * <code>ParsedClassType</code>s are mutable.
 */
public interface ParsedClassType extends ClassType
{
    X10ClassDef def();
    
    /**
     * The <code>Source</code> that this class type
     * was loaded from. Should be <code>null</code> if it was not loaded from
     * a <code>Source</code> during this compilation. 
     */
    Source fromSource();

    boolean defaultConstructorNeeded();
}
