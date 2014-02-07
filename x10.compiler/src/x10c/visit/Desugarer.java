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
import x10.ast.SettableAssign;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10Unary_c;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;
import x10.types.checker.Converter;

/**
 * Visitor to desugar the AST before code gen.
 */
public class Desugarer extends x10.visit.Desugarer {
    private TypeSystem xts;
    private NodeFactory xnf;
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        this.xts = (TypeSystem) ts;
        this.xnf = (NodeFactory) nf;
    }

    @Override
    protected Expr visitSettableAssign(SettableAssign n) {
        return super.visitSettableAssign(n);
    }
}
