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

/**
 * A <code>Prefix</code> represents any node that can be used as the
 * prefix of a <code>Receiver</code>.  If the receiver is a type, its prefix
 * can either be an enclosing type or can be a package.  If the receiver is an
 * expression, its prefix can be either an expression or a type.
 */
public interface Prefix extends Node
{
}
