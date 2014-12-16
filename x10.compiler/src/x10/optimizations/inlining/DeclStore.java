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
import polyglot.ast.Receiver;
import polyglot.ast.Special;
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
import polyglot.util.Position;
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
    private final Map<String, DeclPackage> declPackages;

    private Job job;
    private InlineUtils utils;
    private int implicitMax;
    private boolean initialized;

    private DeclPackage ambiguousDef;

    public DeclStore(TypeSystem ts, NodeFactory nf) {
        this.ts      = ts;
        this.nf      = nf;
        declPackages = CollectionFactory.newHashMap();
        initialized  = false;
        ambiguousDef = new DeclPackage("ProcecureDef does not contain necessary position information to disambiguate ProcedureDecl");
    }

    public void startJob (Job j) {
        job = j;
        if (!initialized) { // this stuff needs a Job (any Job) to initialize but is otherwise final
            initialized     = true;
            utils           = new InlineUtils(job);
            Configuration c = ((X10CompilerOptions) job.extensionInfo().getOptions()).x10_config;
            int impCalls    = c.INLINE_SIZE + (c.EXPERIMENTAL ? 1 : 0);
            implicitMax     = (impCalls+1)*CostDelegate.CALL_COST - 1;
            assert c.OPTIMIZE && (c.INLINE || 0 < c.INLINE_SIZE);
        }
    }

    /**
     * @param call
     * @return
     */
    ProcedureDecl retrieveDecl(InlinableCall call) {
        X10ProcedureDef def = getDef(call);
        DeclPackage pkg = getDeclPackage(def);
        if (null == pkg) {
            try {
                Job job = ((ClassType) Types.baseType(((MemberDef) def).container().get())).def().job();
                if (null == job || null == job.extensionInfo() || null == job.extensionInfo().scheduler())
                    return null;
                Scheduler scheduler = job.extensionInfo().scheduler();
                Goal goal = new Optimizer(scheduler, job).Packager();
                if (!scheduler.attempt(goal)) { // throw ICE ??
                    putDeclPackage(def, new DeclPackage("job for candidate does not compile: " +def));
                    return null;
                }
            } catch (CyclicDependencyException e) { // this should never happen
                putDeclPackage(def, new DeclPackage("job for candidate does not compile: " +def+ " (" +e+ ")"));
                throw new InternalCompilerError("If A ->* B, A is at Harvester & ? -> A would break any cycle", call.position(), e);
            }
            pkg = getDeclPackage(def);
            assert null != pkg : "def="+def+" call="+call+" pos1="+def.position()+" pos2="+call.position(); // FIXME this assertion may fail if a class overrides a final method of its super class.
        }
        boolean nonVirtual = false;
//        nonVirtual |= call instanceof Call && ((Call) call).nonVirtual(); // is this really used // TODO rewrite non-virtual call with super bridge call
//        nonVirtual |= call.target() instanceof Special && ((Special) call.target()).kind() == Special.SUPER; // TODO rewrite non-virtual call with super bridge call
//        nonVirtual |= call instanceof ConstructorCall && ((ConstructorCall) call).kind() == ConstructorCall.SUPER; // TODO rewrite non-virtual call with super bridge call
        if (pkg==null) return null;
        if (!nonVirtual && !pkg.inlinable) return null;
        boolean required = utils.inliningRequired(call) || utils.inliningRequired(def);
        ProcedureDecl decl = pkg.getDecl(required ? Integer.MAX_VALUE : implicitMax, !nonVirtual);
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
    DeclPackage getDeclPackage(X10ProcedureDef def) {
        if (Position.COMPILER_GENERATED == def.position())
            return ambiguousDef;
        return declPackages.get(cannonize(def));
    }

    /**
     * @param def
     * @param pkg
     */
    void putDeclPackage(X10ProcedureDef def, DeclPackage pkg) {
        declPackages.put(cannonize(def), pkg);
    }


    /**
     * @param def
     * @return
     */
    private String cannonize(X10ProcedureDef def) {
        return (def.position().nameAndLineString()+ ": " +def.signature()).intern();
    }

}
