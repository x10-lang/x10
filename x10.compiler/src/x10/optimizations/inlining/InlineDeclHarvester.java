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
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.Flags;
import polyglot.types.MemberDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.types.X10ClassDef;
import x10.types.X10ProcedureDef;
import x10.visit.ExpressionFlattener;

/**
 * @author Bowen Alpern
 * TODO: cache the Decl's and there inline cost info
 *
 */
public class InlineDeclHarvester extends ContextVisitor {

    private DeclStore repository;
    private InlineAnnotationUtils annotations;

    /**
     * @param job
     * @param ts
     * @param nf
     */
    public InlineDeclHarvester(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ContextVisitor#begin()
     */
    @Override
    public NodeVisitor begin() {
        repository = job.compiler().getInlinerData(job, ts, nf);
        annotations = new InlineAnnotationUtils(job);
        return super.begin();
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#enterCall(polyglot.ast.Node)
     */
    @Override
    protected NodeVisitor enterCall(Node n) throws SemanticException {
        if (n instanceof ProcedureDecl) {
            ProcedureDecl   decl = (ProcedureDecl) n;
            X10ProcedureDef pdef = (X10ProcedureDef) decl.procedureInstance();
            X10ClassDef     cdef = ((ClassType) Types.baseType(((MemberDef) pdef).container().get())).def();
            if ( annotations.inliningProhibited(decl) ||
                 annotations.inliningProhibited(cdef) ||
                 annotations.inliningProhibited(pdef) ||
                 ExpressionFlattener.cannotFlatten(n) ||
                 isVirtualOrNative(pdef, cdef)        ||
                 null == decl.body()
               ) {
                repository.cannotInline(pdef);
                return new NodeVisitor() { public Node override(Node n) { return n; } }; // don't visit anything
            }
            return new InlineCostEstimator(job, decl);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ProcedureDecl && v instanceof InlineCostEstimator) {
            ProcedureDecl       dcl = (ProcedureDecl) n;
            X10ProcedureDef     def = (X10ProcedureDef) dcl.procedureInstance();
            InlineCostEstimator ice = (InlineCostEstimator) v;
            assert old == ice.getDecl();
            if (InlineCostEstimator.MAX_ACTUAL_COST < ice.getCost()) {
                repository.cannotInline(def);
            } else {
                repository.putICE(def, ice);
                repository.putDecl(def, dcl);
                repository.putCost(def, ice.getCost());
            }
        }
        return n;
    }

    /**
     * Check that a method is eligible to be inlined.
     * 
     * @param def the method considered for inlining
     * @param container the class containing the def
     * @return true, if the method obviously should not be inlined; false, otherwise
     */
    private boolean isVirtualOrNative(X10ProcedureDef def, ClassDef container) {
        Flags mf = ((MemberDef) def).flags();
        Flags cf = container.flags();
        if (mf.isNative() || cf.isNative())
            return true;
        if (mf.isStatic() || mf.isFinal() || mf.isPrivate() || (def instanceof ConstructorDef))
            return false;
        if ( cf.isFinal() || container.isStruct())
            return false;
        return true;
    }

}
