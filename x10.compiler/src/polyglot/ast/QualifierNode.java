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

package polyglot.ast;

import polyglot.types.Qualifier;
import polyglot.types.Ref;

/**
 * A <code>QualifierNode</code> represents any node that can be used as a type
 * qualifier (<code>polyglot.types.Qualifier</code>).  It can resolve to either
 * an enclosing type or can be a package.
 */
public interface QualifierNode extends Prefix
{
    /** The qualifier type object. */
    Ref<? extends Qualifier> qualifierRef();
}
