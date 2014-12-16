/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.Collections;

import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
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
        // XTENLANG-2660
        Context context =  tc.context();
        Name name = local().name().id();
        if (context.localHasAt(name)) {
            Errors.issue(tc.job(), new Errors.LocalVariableAccessedAtDifferentPlace(name, local().position()));
        }
        //if (local().flags().isFinal()) { // final locals are checked for local access only on assignment (reading a final local can be done from any place)
        //    final X10Local_c local = (X10Local_c) local();
        //    local.checkLocalAccess(local.localInstance(), tc);
        //}
        return Checker.typeCheckAssign(this, tc);
    }

}