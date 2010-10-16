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

package x10.ast;

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.SemanticException;
import x10.types.TypeSystem;

/**
 * @author igor
 */
public class X10Do_c extends Do_c {

    /**
     * @param pos
     * @param body
     * @param cond
     */
    public X10Do_c(Position pos, Stmt body, Expr cond) {
        super(pos, body, cond);
        // TODO Auto-generated constructor stub
    }

    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (! ts.isSubtype(cond.type(), ts.Boolean(), tc.context())) {
            throw new SemanticException("Condition of do statement must have boolean type, and not " + cond.type() + ".",cond.position());
        }

        return this;
    }

}
