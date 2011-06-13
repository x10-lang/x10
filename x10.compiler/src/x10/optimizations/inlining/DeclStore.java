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

import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.frontend.Compiler;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.MemberDef;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.X10CompilerOptions;
import x10.ast.ClosureCall;
import x10.ast.InlinableCall;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.optimizations.ForLoopOptimizer;
import x10.optimizations.Optimizer;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10MemberDef;
import x10.types.X10ProcedureDef;
import x10.util.CollectionFactory;
import x10.visit.ConstructorSplitterVisitor;
import x10.visit.Desugarer;
import x10.visit.ExpressionFlattener;
import x10.visit.IfdefVisitor;
import x10.visit.X10TypeChecker;

/**
 * @author  Bowen Alpern
 */
public class DeclStore {

    private static final boolean DEBUG = false;

    private final TypeSystem ts;
    private final NodeFactory nf;
    private final Set<ProcedureDef> cannotInline ;
    private final Map<ProcedureDef, ProcedureDecl> def2decl;
    private final Map<ProcedureDef, Integer>       def2cost;

    private Job job;
    private InlineAnnotationUtils annotations;
    private boolean implicit;
    private int implicitMax;
    private boolean initialized;

    public DeclStore(TypeSystem ts, NodeFactory nf) {
        this.ts      = ts;
        this.nf      = nf;
        cannotInline = CollectionFactory.newHashSet();
        def2decl     = CollectionFactory.newHashMap();
        def2cost     = CollectionFactory.newHashMap();
        initialized  = false;
    }

    public void startJob (Job j) {
        job = j;
        if (!initialized) { // this stuff needs a Job (any Job) to initialize but is otherwise final
            initialized     = true;
            annotations     = new InlineAnnotationUtils(job);
            Configuration c = ((X10CompilerOptions) job.extensionInfo().getOptions()).x10_config;
            implicit        = c.INLINE_METHODS_IMPLICIT;
            implicitMax     = c.EXPERIMENTAL ? 1 : 0;
            assert c.OPTIMIZE;
        }
    }

    /**
     * @param call
     * @return
     */
    ProcedureDecl retrieveDecl(InlinableCall call) {
        X10ProcedureDef def = getDef(call);
        if (!inlineable(def)) { 
            report("candidate known to be uninlinable: " +def, call);
            return null;
        }
        boolean required = annotations.inliningRequired(call) || annotations.inliningRequired(def);
        if (!required && !implicit) {
            report("inlining not required for candidate: " +def, call);
            return null;
        }
        ProcedureDecl decl = getDecl(call, def);
        if (null == decl) 
            return null;
        if (implicit) {
            int cost = getCost(def);
            if (implicitMax < cost && !required) {
                report("too expensive, cost = " + cost, call);
                return null;
            }
        }
        return decl;
    }

    /**
     * @param call
     * @param def
     * @return
     */
    public ProcedureDecl getDecl(InlinableCall call, X10ProcedureDef def) {
        ProcedureDecl decl = getDecl(def);
        if (null == decl) {
            try {
                Job job = ((ClassType) Types.baseType(((MemberDef) def).container().get())).def().job();
                if (null == job || null == job.extensionInfo() || null == job.extensionInfo().scheduler())
                    return null;
                Scheduler scheduler = job.extensionInfo().scheduler();
                if (!scheduler.attempt(new Optimizer(scheduler, job).Harvester())) { // throw ICE ??
                    report("job for candidate does not compile: " +def, call);
                    cannotInline(def);
                    return null;
                }
            } catch (CyclicDependencyException e) { // this should never happen
                report("job for candidate does not compile: " +def, call);
                cannotInline(def);
                throw new InternalCompilerError("If A ->* B, A is at Harvester & ? -> A would break any cycle", call.position(), e);
            }
            decl = getDecl(def);
        }
        return decl;
    }

    /**
     * @param call
     * @return
     */
    X10ProcedureDef getDef(InlinableCall call) {
        if (call instanceof Call) {
            return (X10ProcedureDef) ((Call) call).methodInstance().def();
        } 
        if (call instanceof ConstructorCall) {
            return (X10ProcedureDef) ((ConstructorCall) call).constructorInstance().def();
        }
        if (call instanceof ClosureCall) {
            return (X10ProcedureDef) ((ClosureCall) call).closureInstance().def();
        }
        return null;
    }

    /**
     * Check that a call is eligible to be inlined.
     * 
     * @param def the procedure def of the callee
     * @return true, if the callee obviously should not be inlined; false, otherwise
     */
    private boolean inlineable(ProcedureDef def) {
        if (null != def2decl.get(def)) return true;
        if (cannotInline.contains(def)) return false;
        if (annotations.inliningProhibited(def)) {
            cannotInline.add(def);
            return false;
        }
        return true;
    }

    public void cannotInline(ProcedureDef def) {
        cannotInline.add(def);
    }

    public ProcedureDecl getDecl(Def def) {
        return def2decl.get(def);
    }

    public void putDecl(ProcedureDef def, ProcedureDecl decl) {
        def2decl.put(def, decl);
    }

    /**
     * @param def
     * @param cost
     */
    public void putCost(ProcedureDef def, int cost) {
        def2cost.put(def, cost);
    }

    /**
     * @param def
     * @return
     */
    private int getCost(X10ProcedureDef def) {
        return def2cost.get(def);
    }

    /**
     * @param string
     * @param ast
     */
    private String report(String string, Node ast) {
//      return inliner.report(string, ast);
        return string;
    }

}