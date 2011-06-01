/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.optimizations.inlining;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.SourceFile;
import polyglot.frontend.Job;
import polyglot.types.ProcedureDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.visit.ExpressionFlattener;

/**
 * @author Bowen Alpern
 * TODO: cache the Decl's and there inline cost info
 *
 */
public class InlineDeclHarvester extends ContextVisitor {

    private final DeclStore repository;
    /**
     * @param job
     * @param ts
     * @param nf
     */
    public InlineDeclHarvester(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        repository = job.extensionInfo().compiler().getInlinerData(job, ts, nf);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#enterCall(polyglot.ast.Node)
     */
    @Override
    protected NodeVisitor enterCall(Node n) throws SemanticException {
        if (n instanceof ProcedureDecl) {
//          return new InlineCostVisitor(job, ts, nf, new InlineCostEstimator(job, ts, nf));
        }
        return this;
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ProcedureDecl) {
            ProcedureDecl pd = (ProcedureDecl) n;
            repository.putDecl(pd.procedureInstance(), pd);
        }
        return n;
    }

}
