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

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.types.Context;
import polyglot.types.Type;

/**
 * <code>RedundantCastRemover</code> removes redundant casts.  It's typically
 * used to clean up inefficient translations from the source language to Java.
 * The AST must be type-checked before using this visitor.
 */
public class RedundantCastRemover extends NodeVisitor {
    public RedundantCastRemover() {
        super();
    }
    
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof Cast) {
            Cast c = (Cast) n;
            Type castType = c.castType().type();
            Type exprType = c.expr().type();
            Context context = castType.typeSystem().emptyContext();
            if (exprType.isImplicitCastValid(castType, context)) {
                // Redundant cast.
                return c.expr();
            }
        }

        // Do not remove redundant casts from call arguments since the
        // cast may be there to resolve an ambiguity or to force another
        // overloaded method to be called.
        if (n instanceof ProcedureCall) {
            ProcedureCall newCall = (ProcedureCall) n;
            ProcedureCall oldCall = (ProcedureCall) old;
            List<Expr> newArgs = new ArrayList<Expr>(newCall.arguments().size());
            boolean changed = false;
            Iterator<Expr> i = newCall.arguments().iterator();
            Iterator<Expr> j = oldCall.arguments().iterator();
            while (i.hasNext() && j.hasNext()) {
                Expr newE = i.next();
                Expr oldE = j.next();
                if (oldE instanceof Cast) {
                    newArgs.add(oldE);
                    changed = true;
                }
                else {
                    newArgs.add(newE);
                }
            }
            if (changed) {
                n = newCall.arguments(newArgs);
            }
        }
        return n;
    }
}
