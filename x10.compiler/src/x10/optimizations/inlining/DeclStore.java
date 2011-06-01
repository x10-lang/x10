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

    private Job job;
    private InlineCostEstimator ice;
    private ExtensionInfo extInfo;
    private Compiler compiler;
    private boolean INLINE_IMPLICIT;
    private boolean INLINE_STRUCT_CONSTRUCTORS;
    private int implicitMax;
    private Set<ProcedureDef> cannotInline ;
    private Map<ProcedureDef, ProcedureDecl> def2decl;
    private boolean firstTime = false;

    public DeclStore(TypeSystem ts, NodeFactory nf) {
        this.ts      = ts;
        this.nf      = nf;
        cannotInline = CollectionFactory.newHashSet();
        def2decl     = CollectionFactory.newHashMap();
        firstTime    = true;
    }

    public void startJob (Job j) {
        job = j;
        ice = new InlineCostEstimator(j, ts, nf);
        if (firstTime) {
            firstTime                  = false;
            extInfo                    = j.extensionInfo();
            compiler                   = extInfo.compiler();
            Configuration config       = ((X10CompilerOptions) extInfo.getOptions()).x10_config;
            INLINE_IMPLICIT            = config.EXPERIMENTAL && config.OPTIMIZE && config.INLINE_METHODS_IMPLICIT;
            INLINE_STRUCT_CONSTRUCTORS = config.INLINE_STRUCT_CONSTRUCTORS;
         // implicitMax                = config.EXPERIMENTAL ? 1 : 0;
            implicitMax                = 0;
        }
    }

    /**
     * @param call
     * @return
     */
    ProcedureDecl findDecl(InlinableCall call) {
        Position pos = call.position();
        X10ProcedureDef def = getDef(call);
        if (!inlineable(def)) { 
            report("candidate known to be uninlinable: " +def, call);
            return null;
        }
        boolean required = Inliner.annotationsRequireInlining(call) || annotationsRequireInlining(def);
        if (!required && !INLINE_IMPLICIT && !(INLINE_STRUCT_CONSTRUCTORS && isStructConstructor(call))) {
            report("inlining not required for candidate: " +def, call);
            return null;
        }
        // unless required, don't inline if the candidate annotations prevent it
        if (annotationsPreventInlining(def)) {
            report("annotation prohibits inlining candidate: " + def, call);
            cannotInline(def);
            return null;
        }
        // get container and declaration for inline candidate
        X10ClassDef container = getContainer(def);
        if (null == container) {
            report("no container found for candidate: " +def, call);
            cannotInline(def);
            return null;
        }
        if (annotationsPreventInlining(container)) {
            report("annotations prohibit inlining from container: " +container, call);
            cannotInline(def);
            return null;
        }
        if (isVirtualOrNative(def, container)) {
            report("call is virtual or native on candidate: " +def, call);
            cannotInline(def);
            return null;
        }
        Job candidateJob = container.job();
        if (null == candidateJob) {
            report("no job found for candidate: " +def, call);
            cannotInline(def);
            return null;
        }
        ProcedureDecl decl = getDecl(def);
        if (null == decl) {
            try {
                String source = candidateJob.source().toString().intern();
                Scheduler scheduler = extInfo.scheduler();
                Optimizer opt = new Optimizer(scheduler, candidateJob);
                Goal harvester = opt.Harvester();
                if (!scheduler.attempt(harvester)) { // throw ICE ??
                    report("job for candidate does not compile: " +def, call);
                    cannotInline(def);
                    return null;
                }
            } catch (CyclicDependencyException e) {
                report("job for candidate does not compile: " +def, call);
                cannotInline(def);
                throw new InternalCompilerError("If A ->* B, then A is at Harvester and ? -> A would break any cycle", pos, e);
            }
            decl = getDecl(def);
            if (null == decl) {
                report("no decl found for candidate: " +def, call);
                cannotInline(def);
                return null;
            }
        }
        /* these tests are redundant
        if (Inliner.annotationsPreventInlining(decl) || annotationsPreventInlining(decl.procedureInstance())) {
            report("candidate declaration annotated to prevent inlining: " +candidate, call);
            notInlinable(candidate);
            return null;
        }
        */
        if (null == decl.body()) {
            report("candidate has empty body: " +def, call);
            cannotInline(def);
            return null;
        }
        if (ExpressionFlattener.cannotFlatten(decl)){
            report("candidate cannot be flattened: " +def, call);
            cannotInline(def);
            return null;
        }
        if (ExpressionFlattener.javaBackend(job) && hasSuperTarget(decl)) {
            report("candidate body contains super (not yet supported by Java backend): " +def, call);
            cannotInline(def);
            return null;
        }
        if (!required && (INLINE_IMPLICIT || (INLINE_STRUCT_CONSTRUCTORS && isStructConstructor(call)))) {
            int cost = getCost(decl, candidateJob);
            if (implicitMax < cost) {
                report("of excessive cost, " + cost, call);
                cannotInline(def);
                return null;
            }
        } else if (!required) {
            report("inlining not explicitly required", call);
            return null;
        }
        return decl;
    }

    /**
     * @param call
     * @return
     */
    private boolean isStructConstructor(InlinableCall call) {
        if (!(call instanceof ConstructorCall)) return false;
        return ts.isStruct(((ConstructorCall) call).constructorInstance().returnType());
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
     * Get the definition of the Class that implements a given method.
     * 
     * @param candidate the method definition whose container is desired
     * @return the definition of the Class containing md
     */
    private X10ClassDef getContainer(ProcedureDef candidate) {
        Ref<? extends ContainerType> containerRef = ((MemberDef) candidate).container();
        ContainerType containerType = Types.get(containerRef);
        Type containerBase = Types.baseType(containerType); 
        assert (containerBase instanceof X10ClassType);
        X10ClassDef container = ((ClassType) containerBase).def();
        return container;
    }

    /**
     * Check that a candidate method is eligible to be inlined.
     * 
     * @param candidate the method conside red for inlining
     * @param container the class containing the candidate
     * @return true, if the method obviously should not be inlined; false, otherwise
     */
    private boolean isVirtualOrNative(X10ProcedureDef candidate, ClassDef container) {
        Flags mf = ((MemberDef) candidate).flags();
        Flags cf = container.flags();
        if (mf.isNative() || cf.isNative())
            return true;
        if (!mf.isFinal() && !mf.isPrivate() && !mf.isStatic() && !cf.isFinal() && !container.isStruct() && !(candidate instanceof ConstructorDef))
            return true;
        return false;
    }

    /**
     * @param node
     * @return
     */
    private boolean hasSuperTarget(Node node) {
        SuperFinderVisitor visitor = new SuperFinderVisitor();
        node.visit(visitor);
        return visitor.hasSuper;
    }

    /**
     * @param decl
     * @param job
     * @return
     */
    private int getCost(ProcedureDecl decl, Job job) {
        int cost = ice.getCost(decl, job);
        return cost;
    }

    private boolean inlineable(ProcedureDef def) {
        return !cannotInline.contains(def);
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
     * Annotation types.
     */
    private static Type InlineType;
    private static Type NoInlineType;
    private static Type NativeType;
    private static Type NativeRepType;
    private static Type NativeClassType;

    /**
     * Names of the annotation types that pertain to inlining.
     */
    private static final QName INLINE_ANNOTATION       = QName.make("x10.compiler.Inline");
    private static final QName NO_INLINE_ANNOTATION    = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");
    
    /**
     * @throws InternalCompilerError
     */
    void initializeAnnotationTypes() throws InternalCompilerError {
        try {
            InlineType = ts.systemResolver().findOne(INLINE_ANNOTATION);
            NoInlineType = ts.systemResolver().findOne(NO_INLINE_ANNOTATION);
            NativeType = ts.NativeType();
            NativeClassType = ts.systemResolver().findOne(NATIVE_CLASS_ANNOTATION);
            NativeRepType = ts.NativeRep();
        } catch (SemanticException e) {
            InternalCompilerError ice = new InternalCompilerError("Unable to find required Annotation Type", e);
            Errors.issue(job, new SemanticException(ice));
            throw ice; // annotation types are required!
        }
    }

    /**
     * @param call
     * @param candidate
     * @return
     */
    static boolean annotationsRequireInlining(X10ProcedureDef candidate) {
        if (!candidate.annotationsMatching(InlineType).isEmpty())
            return true;
        return false;
    }

    /**
     * @param def
     * @return
     */
    static boolean annotationsPreventInlining(X10Def def) {
        if (!def.annotationsMatching(NoInlineType).isEmpty())
            return true;
        if (!def.annotationsMatching(NativeRepType).isEmpty())
            return true;
        if (!def.annotationsMatching(NativeClassType).isEmpty())
            return true;
        return false;
    }

    /**
     * @param report
     * @param object
     */
    private void debug(String report, Node node) {
        Inliner.debug(report, node);
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