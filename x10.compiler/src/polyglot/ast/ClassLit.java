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

/**
 * A <code>ClassLit</code> represents a class literal expression. 
 * A class literal expressions is an expression consisting of the 
 * name of a class, interface, array, or primitive type followed by a period (.) 
 * and the token class. 
 */
public interface ClassLit extends Lit {
    TypeNode typeNode();
}
