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

import java.util.Collections;

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.LocalInstance;
import x10.types.Name;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.X10LocalInstance;
import x10.types.checker.Checker;

public class X10LocalAssign_c extends LocalAssign_c {

    public X10LocalAssign_c(NodeFactory nf, Position pos, Local left, Operator op, Expr right) {
        super(nf, pos, left, op, right);
    }

    @Override
    public Type leftType() {
        LocalInstance li = local().localInstance();
        if (li == null)
            return null;
        if (li instanceof X10LocalInstance) {
            return ((X10LocalInstance) li).type();
        }
        return li.type();
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        return Checker.typeCheckAssign(this, tc);
    }

}