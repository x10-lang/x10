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

import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.ClassType;
import polyglot.types.MemberDef;
import polyglot.types.ProcedureDef;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import x10.Configuration;
import x10.X10CompilerOptions;
import x10.ast.ClosureCall;
import x10.ast.InlinableCall;
import x10.optimizations.Optimizer;
import x10.types.X10ProcedureDef;
import x10.util.CollectionFactory;

/**
 * @author  Bowen Alpern
 */
public class DeclStore {

    private static final boolean DEBUG = false;

    private final TypeSystem ts;
    private final NodeFactory nf;
    private final Map<ProcedureDef, InlineCostEstimator> def2ICE;

    private Job job;
    private InlineAnnotationUtils annotations;
    private boolean implicit;
    private int implicitMax;
    private boolean initialized;

    public DeclStore(TypeSystem ts, NodeFactory nf) {
        this.ts      = ts;
        this.nf      = nf;
        def2ICE      = CollectionFactory.newHashMap();
        initialized  = false;
    }

    public void startJob (Job j) {
        job = j;
        if (!initialized) { // this stuff needs a Job (any Job) to initialize but is otherwise final
            initialized     = true;
            annotations     = new InlineAnnotationUtils(job);
            Configuration c = ((X10CompilerOptions) job.extensionInfo().getOptions()).x10_config;
            implicit        = c.INLINE_METHODS_IMPLICIT;
      //    implicitMax     = c.EXPERIMENTAL ? 1 : 0;
            implicitMax     = (c.EXPERIMENTAL ? 3 : 2)*InlineCostDelegate.CALL_COST - 1;
            assert c.OPTIMIZE;
        }
    }

    /**
     * @param call
     * @return
     */
    ProcedureDecl retrieveDecl(InlinableCall call) {
        X10ProcedureDef def = getDef(call);
        InlineCostEstimator ice = getICE(def);
        if (null == ice) {
            try {
                Job job = ((ClassType) Types.baseType(((MemberDef) def).container().get())).def().job();
                if (null == job || null == job.extensionInfo() || null == job.extensionInfo().scheduler())
                    return null;
                Scheduler scheduler = job.extensionInfo().scheduler();
                Goal goal = new Optimizer(scheduler, job).Harvester();
                if (!scheduler.attempt(goal)) { // throw ICE ??
                    report("job for candidate does not compile: " +def, call);
                    cannotInline(def);
                    return null;
                }
            } catch (CyclicDependencyException e) { // this should never happen
                report("job for candidate does not compile: " +def, call);
                cannotInline(def);
                throw new InternalCompilerError("If A ->* B, A is at Harvester & ? -> A would break any cycle", call.position(), e);
            }
            ice = getICE(def);
        }
        if (!ice.inlinable) return null;
        boolean required = annotations.inliningRequired(call) || annotations.inliningRequired(def);
        ProcedureDecl decl = ice.getDecl(required ? Integer.MAX_VALUE : implicit ? implicitMax : 0);
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
     * @param def
     * @return
     */
    InlineCostEstimator getICE(X10ProcedureDef def) {
        return def2ICE.get(def);
    }

    /**
     * @param def
     * @param ice
     */
    void putICE(X10ProcedureDef def, InlineCostEstimator ice) {
        def2ICE.put(def, ice);
    }

    /**
     * @param def
     */
    public void cannotInline(X10ProcedureDef def) {
        putICE(def, new InlineCostEstimator());
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