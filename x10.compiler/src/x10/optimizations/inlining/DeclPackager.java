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
import polyglot.util.Position;
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
public class DeclPackager extends ContextVisitor {

    private DeclStore repository;
    private InlineUtils utils;

    /**
     * @param job
     * @param ts
     * @param nf
     */
    public DeclPackager(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ContextVisitor#begin()
     */
    @Override
    public NodeVisitor begin() {
        repository = job.compiler().getInlinerData(job, ts, nf);
        utils = new InlineUtils(job);
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
            DeclPackage pkg;
            if ( (Position.COMPILER_GENERATED == n.position()) ||
                 utils.inliningProhibited(decl) ||
                 utils.inliningProhibited(cdef) ||
                 utils.inliningProhibited(pdef) ||
                 ExpressionFlattener.cannotFlatten(n, job) ||
                 isNative(pdef, cdef) ||
                 null == decl.body()
               ) {
                pkg = new DeclPackage("Call is uninlinable"); // inlining prohibited
                repository.putDeclPackage(pdef, pkg);
            } else if (isVirtual(pdef, cdef)) {
                pkg = new DeclPackage("Only non-virtual call is inlinable", job, decl);
                repository.putDeclPackage(pdef, pkg);
            } else {
                pkg = new DeclPackage(job, decl);
            }
            return pkg;
        }
        return this;
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ProcedureDecl && v instanceof DeclPackage) {
            X10ProcedureDef def = (X10ProcedureDef) ((ProcedureDecl) n).procedureInstance();
            repository.putDeclPackage(def, (DeclPackage) v);
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
    private boolean isNative(X10ProcedureDef def, ClassDef container) {
        Flags mf = ((MemberDef) def).flags();
        Flags cf = container.flags();
        if (mf.isNative() || cf.isNative())
            return true;
        return false;
    }

    /**
     * Check that a method is eligible to be inlined for non-virtual call.
     * 
     * @param def the method considered for inlining
     * @param container the class containing the def
     * @return true, if the method obviously should not be inlined; false, otherwise
     */
    private boolean isVirtual(X10ProcedureDef def, ClassDef container) {
        Flags mf = ((MemberDef) def).flags();
        Flags cf = container.flags();
        if (mf.isStatic() || mf.isFinal() || mf.isPrivate() || (def instanceof ConstructorDef))
            return false;
        if ( cf.isFinal() || container.isStruct())
            return false;
        return true;
    }

}
