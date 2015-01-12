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

import polyglot.types.*;

/**
 * A <code>FieldAssign</code> represents a Java assignment expression to
 * a field.  For instance, <code>this.x = e</code>.
 * 
 * The class of the <code>Expr</code> returned by
 * <code>FieldAssign.left()</code>is guaranteed to be a <code>Field</code>.
 */
public interface FieldAssign extends Assign
{
    public Receiver target();
    public FieldAssign target(Receiver target);

    public Id name();
    public FieldAssign name(Id name);

    public FieldAssign fieldInstance(FieldInstance fi);
    public FieldInstance fieldInstance();
    
    public boolean targetImplicit();
    FieldAssign targetImplicit(boolean f);
}
