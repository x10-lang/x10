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
 * A <code>ArrayAccessAssign</code> represents a Java assignment expression
 * to an array element.  For instance, <code>A[3] = e</code>.
 * 
 * The class of the <code>Expr</code> returned by
 * <code>ArrayAccessAssign.left()</code>is guaranteed to be an
 * <code>ArrayAccess</code>.
 */
public interface ArrayAccessAssign extends Assign
{
    boolean throwsArrayStoreException();

    Expr array();
    ArrayAccessAssign array(Expr array);
    
    Expr index();
    ArrayAccessAssign index(Expr index);

}
