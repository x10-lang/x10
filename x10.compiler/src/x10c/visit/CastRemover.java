/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10c.visit;

import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Cast;
import x10.types.ConstrainedType;
import polyglot.types.TypeSystem;

public class CastRemover extends ContextVisitor {
    
    private final TypeSystem xts;
    private final NodeFactory xnf;
    
    public CastRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Assign_c) {
            return removeRedundantCast((Assign_c) n);
        }
        return n;
    }

    private Node removeRedundantCast(Assign_c assign) {
        Expr right = assign.right();
        if (right instanceof X10Cast) {
            X10Cast cast = (X10Cast) right;
            TypeNode castType = cast.castType();
            Expr expr = cast.expr();
            Type type = expr.type();
            // e.g. i:Int = (Int) j:Int{self==0} --> i:Int = j:Int{self==0};
            if (assign.leftType().typeEquals(castType.type(), context) && castType.type().typeEquals(Types.baseType(type), context)) {
                return assign.right(expr);
            }
        }
        return assign;
    }
}
