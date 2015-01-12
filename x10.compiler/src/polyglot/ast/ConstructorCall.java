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

import java.util.List;

import polyglot.types.ConstructorInstance;
import x10.ast.InlinableCall;

/**
 * A <code>ConstructorCall</code> represents a direct call to a constructor.
 * For instance, <code>super(...)</code> or <code>this(...)</code>.
 */
public interface ConstructorCall extends Stmt, InlinableCall
{
    /** Constructor call kind: either "super" or "this". */
    public static enum Kind {
        SUPER("super"), THIS("this");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }
    }

    public static final Kind SUPER = Kind.SUPER;
    public static final Kind THIS  = Kind.THIS;

    /** The qualifier of the call, possibly null. */
    Expr qualifier();

    /** Set the qualifier of the call, possibly null. */
    ConstructorCall qualifier(Expr qualifier);

    /** The kind of the call: THIS or SUPER. */
    Kind kind();

    /** Set the kind of the call: THIS or SUPER. */
    ConstructorCall kind(Kind kind);

    /**
     * Actual arguments.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> arguments();

    /**
     * Set the actual arguments.
     * @param arguments A list of {@link polyglot.ast.Expr Expr}.
     */
    ConstructorCall arguments(List<Expr> arguments);

    /**
     * The constructor that is called.  This field may not be valid until
     * after type checking.
     */
    ConstructorInstance constructorInstance();

    /** Set the constructor to call. */
    ConstructorCall constructorInstance(ConstructorInstance ci);

    /**
     * The target of the constructor call.
     * Until the ConstructorSplitterVisitor runs (after type checking), this call returns null.  
     * Afterwards, it will return either an X10Allocation or this.
     */
    Expr target();

    
    /**
     * Sets the target of this constructor call.
     * 
     * @param target the target (must be either X10Allocation or this.
     * @return the modified constructor call.
     */
    ConstructorCall target(Expr target);
}
