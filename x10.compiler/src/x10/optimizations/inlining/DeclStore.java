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

import polyglot.ast.ConstructorCall;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.MemberDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.X10CompilerOptions;
import x10.ast.InlinableCall;
import x10.errors.Warnings;
import x10.optimizations.ForLoopOptimizer;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
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

    private Inliner inliner;
    private Job job;
    private final TypeSystem ts;
    private final NodeFactory nf;
    private final InlineCostEstimator ice;
    private final ExtensionInfo extInfo;
    private final Compiler compiler;
            final boolean INLINE_IMPLICIT;
    private final boolean INLINE_STRUCT_CONSTRUCTORS;
    private final int implicitMax;


    public DeclStore(Inliner in) {
        inliner                    = in;
        job                        = in.job();
        ts                         = in.typeSystem();
        nf                         = in.nodeFactory();
        ice                        = new InlineCostEstimator(job, ts, nf);
        extInfo                    = job.extensionInfo();
        compiler                   = extInfo.compiler();
        Configuration config       = ((X10CompilerOptions) extInfo.getOptions()).x10_config;
        INLINE_IMPLICIT            = config.EXPERIMENTAL && config.OPTIMIZE && config.INLINE_METHODS_IMPLICIT;
        INLINE_STRUCT_CONSTRUCTORS = config.INLINE_STRUCT_CONSTRUCTORS;
     // implicitMax                = config.EXPERIMENTAL ? 1 : 0;
        implicitMax                = 0;
    }

    /**
     * @param call
     * @param candidate
     * @return
     */
    ProcedureDecl findDecl(InlinableCall call, MemberDef candidate, boolean required) {
        // see if the declaration for this candidate has already been cached
        ProcedureDecl decl = getCache().getDecl(candidate);
        if (null != decl) {
            return decl;
        }
        // get container and declaration for inline candidate
        ClassDef container = getContainer(candidate);
        if (null == container) {
            report("unable to find container for candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (isVirtualOrNative(candidate, container)) {
            report("call is virtual or native on candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (Inliner.annotationsPreventInlining((X10ClassDef) container)) {
            report("of Native Class/Rep annotation of container: " +container, call);
            getCache().notInlinable(candidate);
            return null;
        }
        Job candidateJob = container.job();
        if (null == candidateJob) {
            // reconstruct job from position
            report("unable to find job for candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (candidateJob.reportedErrors()) {
            Warnings.issue(candidateJob, "Has reported compilation errors", call.position());
        }
        Node ast = getAST(candidateJob);
        if (null == ast) {
            report("unable to find valid ast for candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            getCache().badJob(candidateJob);
            return null;
        }
        if (this.job.compiler().errorQueue().hasErrors()) { // This may be overly conservative
            report("there were errors compiling candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            getCache().badJob(candidateJob);
            return null;
        }
        decl = getDeclaration(candidate, ast);
        if (null == decl) {
            report("unable to find declaration for candidate: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (Inliner.annotationsPreventInlining(decl) || Inliner.annotationsPreventInlining(decl.procedureInstance())) {
            report("candidate declaration annotated to prevent inlining: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (ExpressionFlattener.javaBackend(job) && hasSuper(decl)) {
            report("candidate body contains super (not yet supported by Java backend): " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (!required && (INLINE_IMPLICIT || (call instanceof ConstructorCall && ts.isStruct(((ConstructorCall) call).constructorInstance().returnType()) && INLINE_STRUCT_CONSTRUCTORS))) {
            int cost = getCost(decl, candidateJob);
            if (implicitMax < cost) {
                report("of excessive cost, " + cost, call);
                getCache().notInlinable(candidate);
                return null;
            }
        } else if (!required) {
            report("inlining not explicitly required", call);
            getCache().notInlinable(candidate);
            return null;
        }
        // remember what to inline this candidate with if we ever see it again
        getCache().putDecl(candidate, decl);
        return decl;
    }

    /**
     * Get the definition of the Class that implements a given method.
     * 
     * @param candidate the method definition whose container is desired
     * @return the definition of the Class containing md
     */
    private ClassDef getContainer(MemberDef candidate) {
        Ref<? extends ContainerType> containerRef = candidate.container();
        ContainerType containerType = Types.get(containerRef);
        Type containerBase = Types.baseType(containerType); 
        assert (containerBase instanceof X10ClassType);
        ClassDef container = ((ClassType) containerBase).def();
        return container;
    }

    /**
     * Check that a candidate method is eligible to be inlined.
     * 
     * @param candidate the method conside red for inlining
     * @param container the class containing the candidate
     * @return true, if the method obviously should not be inlined; false, otherwise
     */
    private boolean isVirtualOrNative(MemberDef candidate, ClassDef container) {
        Flags mf = candidate.flags();
        Flags cf = container.flags();
        if (mf.isNative() || cf.isNative())
            return true;
        if (!mf.isFinal() && !mf.isPrivate() && !mf.isStatic() && !cf.isFinal() && !container.isStruct() && !(candidate instanceof ConstructorDef))
            return true;
        return false;
    }

    /**
     * @param job
     * @return
     */
    private Node getAST(Job job) {
        String source = job.source().toString().intern();
        InlinerCache cache = getCache();
        Node ast = cache.getAST(source);
        if (null == ast) {
            try {
                if (job == this.job) {
                    ast = job.ast();
                } else {
                    ast = processAST(job);
                }
                assert null != ast;
            } catch (Exception x) {
                report("unable to process " + job, job.ast());
                cache.badJob(job);
                return null;
            }
            cache.putAST(source, ast);
        }
        return ast;
    }



    /**
     * @param job
     * @return
     */
    private Node processAST(Job job) {
        Node ast = job.ast();
        ast = ast.visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin());
        ast = ast.visit(new IfdefVisitor(job, ts, nf).begin());
        ast = ast.visit(new Desugarer(job, ts, nf).begin());
  //    ast = ast.visit(new X10InnerClassRemover(job, ts, nf).begin());
        ast = ast.visit(new ForLoopOptimizer(job, ts, nf).begin());
        if (x10.optimizations.Optimizer.CONSTRUCTOR_SPLITTING(job.extensionInfo()))
            ast = ast.visit(new ConstructorSplitterVisitor(job, ts, nf).begin());
        return ast;
    }

    /**
     * Walk an AST looking for the declaration of a given method.
     * 
     * @param candidate the method whose declaration is desired
     * @param ast the abstract syntax tree containing the declaration
     * @return the declaration for the indicated method, or 
     *         null if no declaration can be found or it has an empty body.
     */
    private ProcedureDecl getDeclaration(final Def candidate, final Node ast) {
        final Position pos = candidate.position();
        final ProcedureDecl[] decl = new ProcedureDecl[1];
        ast.visit(new NodeVisitor() { // find the declaration of md
                    public Node override(Node n) {
                        if (null != decl[0])
                            return n; // we've already found the decl, short-circuit search
                        if (n instanceof TypeNode)
                            return n; // TypeNodes don't contain decls, short-circuit search
                        if (!pos.isCompilerGenerated()&& !contains(n.position(), pos))
                            return n; // definition of md isn't inside n, short-circuit search
                        if (n instanceof ProcedureDecl && candidate == ((ProcedureDecl) n).procedureInstance()) {
                            decl[0] = (ProcedureDecl) n;
                            return n; // we found the decl for the candidate, short-circuit search
                        }
                        return null; // look for the decl inside n
                    }
                });
        if (null == decl[0]) {
            if (DEBUG) debug(report("declaration not found for " +candidate, null), null);
            return null;
        }
        if (null == decl[0].body()) {
            if (DEBUG) debug(report("no declaration body for " +decl[0]+ " (" +candidate+ ")", null), null);
            return null;
        }
        if (ExpressionFlattener.cannotFlatten(decl[0], job)) {
             if (DEBUG) debug(report("unflattenable declaration body for " +decl[0]+ "  (" +candidate+ ")", null), null);
            return null;
        }
        return decl[0];
    }

    /**
     * @param node
     * @return
     */
    private boolean hasSuper(Node node) {
        SuperFinderVisitor visitor = new SuperFinderVisitor();
        node.visit(visitor);
        return visitor.hasSuper;
    }

    // TODO: move this to Position
    private static boolean contains(Position outer, Position inner) {
        if (!outer.file().equals(inner.file()))
            return false;
        if (!outer.path().equals(inner.path()))
            return false;
        return (outer.offset() <= inner.offset() && inner.endOffset() <= inner.endOffset());
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

    final InlinerCache getCache() {
        return compiler.getInlinerCache();
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
        return inliner.report(string, ast);
    }

}