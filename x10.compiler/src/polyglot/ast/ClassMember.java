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

import polyglot.types.MemberDef;

/**
 * A <code>ClassMember</code> is a method, a constructor, a field, an
 * initializer block, or another class declaration.  It is any node that may
 * occur directly inside a class body.
 */
public interface ClassMember extends Term 
{
    public MemberDef memberDef();
}
