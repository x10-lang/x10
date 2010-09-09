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

package x10c.visit;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.NodeFactory;
import polyglot.ast.Unary.Operator;
import polyglot.frontend.Job;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10NodeFactory;
import x10.ast.X10Unary_c;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;

/**
 * Visitor to desugar the AST before code gen.
 */
public class Desugarer extends x10.visit.Desugarer {
    private X10TypeSystem xts;
    private X10NodeFactory xnf;
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        this.xts = (X10TypeSystem) ts;
        this.xnf = (X10NodeFactory) nf;
    }

    @Override
    protected Expr visitSettableAssign(SettableAssign_c n) throws SemanticException {
        if (n.operator() != Assign.ASSIGN) {
            X10Call left = (X10Call) n.left(xnf, this);
            if ((n.type().isBoolean() || n.type().isNumeric()) && (xts.isRail(left.target().type()) || xts.isValRail(left.target().type()))) {
                return n;
            }
        }
        return super.visitSettableAssign(n);
    }
}
