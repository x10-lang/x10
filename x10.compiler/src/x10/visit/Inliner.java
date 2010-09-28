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

package x10.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import polyglot.ast.AmbAssign;
import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Call_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.FunctionDef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SilentErrorQueue;
import polyglot.util.SubtypeSet;
import polyglot.visit.AlphaRenamer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.ParExpr;
import x10.ast.StmtExpr;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.constraint.XFailure;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;

/**
 * This visitor inlines calls to methods and closures under the following
 * conditions:
 * <ul>
 * <li>The exact class of the method target is known.
 * <li>The method being invoked is annotated @X10.compiler.Inline or
 * the method is found to be "small".
 * <li>The closure call target is a literal closure.
 * </ul>
 * 
 * TODO: allow inlined code access to otherwise inaccessible fields and methods.
 * (Set ALLOW_STMTEXPR to false to produce valid X10 code.)
 * 
 * @author nystrom
 * @author igor
 * @author Bowen Alpern (alpernb@us.ibm.com)
 */
public class Inliner extends ContextVisitor {

//  private static final boolean VERBOSE = false;
    private static final boolean VERBOSE = true;
    private static final boolean VERY_VERBOSE = VERBOSE && false;
//  private static final boolean VERY_VERBOSE = VERBOSE && true;
    private static String reason;               //  move these (DEBUG)
    private static boolean inliningRequired;    //  move these (DEBUG)
     
    private static final boolean DEBUG = false;
//  private static final boolean DEBUG = true;

    private void debug (String msg, Node node) {
        if (!DEBUG) return;
        
        try {
          Thread.sleep(10);
          System.out.print("DEBUG ");
          if (null != node) System.out.print(node.position()+ ": ");
          System.out.println(msg);
          Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore exception (we are just trying to avoid stepping on writes to STDERR
        }
        
    }
    
    /**
     * This constant controls accesses to inaccessible fields.
     */
    private static final boolean ALLOW_STMTEXPR = true;

    /**
     * Names of the annotation classes that govern inlining.
     */
    private static final QName INLINE_ANNOTATION        = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION   = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION     = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_ANNOTATION        = QName.make("x10.compiler.Native");
    private static final QName NATIVE_REP_ANNOTATION    = QName.make("x10.compiler.NativeRep");
    private static final QName NATIVE_CLASS_ANNOTATION  = QName.make("x10.compiler.NativeClass");

    /**
     * The cached type of the @Inline and @NoInline annotations.
     */
    private Type InlineType;
    private Type InlineOnlyType;
    private Type NoInlineType;
    private Type NativeType;
    private Type NativeRepType;
    private Type NativeClassType;

    /**
     * The size of the largest method to be considered small, if small methods are to be inlined.
     */
//  private static final int  SMALL_METHOD_MAX_SIZE = -1;
//  private static final int  SMALL_METHOD_MAX_SIZE =  0;
    private static final int  SMALL_METHOD_MAX_SIZE =  1;
//  private static final int  SMALL_METHOD_MAX_SIZE =  2;

    /**
     * Explicit inlining (via annotations) and inlining of small methods (if enabled)
     * is done to an arbitrary depth for non-recursive calls.  There is a somewhat hoaky
     * mechanism for limiting the number of recursive calls that are inlined.
     * 
     * TODO: implement a space budget based policy mechanism for controling inlining.
     */
    private static final Stack<String> inlineInstances = new Stack<String>();
    private static final int recursionDepth[]          = new int[1];
    private static final int INITIAL_RECURSION_DEPTH   = 0;
    private static final int RECURSION_DEPTH_LIMIT     = 2;

    /**
     * Cache the decision to inline a method and the declaration to use for it.
     * TODO: anchor these caches in a compiler object using soft references
     * TODO: reconstruct Job from position, if the job field of a X10ClassDecl is null
     */
    private static final Set<X10MethodDef> dontInline              = new HashSet<X10MethodDef>();
    private static final Map<X10MethodDef, X10MethodDecl> def2decl = new HashMap<X10MethodDef, X10MethodDecl>();
    private static final Set<Job> badJobs                          = new HashSet<Job>();
    private static final Map<String, Node> astMap                  = new HashMap<String, Node>();

    /**
     * 
     */
    private X10TypeSystem xts;
    private X10NodeFactory xnf;
//  private Synthesizer syn;
    private ForLoopOptimizer syn; // move functionality to Synthesizer
    private InlineCostEstimator ice;

    public Inliner(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
//        syn = new Synthesizer(xnf, xts);
        syn = new ForLoopOptimizer(job, ts, nf);
        ice = new InlineCostEstimator(xts, xnf);
    }

    @Override
    public NodeVisitor begin() {
        recursionDepth[0] = INITIAL_RECURSION_DEPTH;
        try {
            InlineType = (Type) ts.systemResolver().find(INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +INLINE_ANNOTATION+ ": "+e);
            InlineType = null;
        }
        try {
            InlineOnlyType = (Type) ts.systemResolver().find(INLINE_ONLY_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +INLINE_ONLY_ANNOTATION+ ": "+e);
            InlineOnlyType = null;
        }
        try {
            NoInlineType = (Type) ts.systemResolver().find(NO_INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +NO_INLINE_ANNOTATION+ ": "+e);
            NoInlineType = null;
        }
        try {
            NativeType = (Type) ts.systemResolver().find(NATIVE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +NATIVE_ANNOTATION+ ": "+e);
            NativeType = null;
        }
        try {
            NativeRepType = (Type) ts.systemResolver().find(NATIVE_REP_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +NATIVE_REP_ANNOTATION+ ": "+e);
            NativeRepType = null;
        }
        try {
            NativeClassType = (Type) ts.systemResolver().find(NATIVE_CLASS_ANNOTATION);
        }
        catch (SemanticException e) {
            System.err.println("Unable to find " +NATIVE_CLASS_ANNOTATION+ ": "+e);
            NativeClassType = null;
        }
        return super.begin();
    }

    public Node override(Node node) {
        if (node instanceof X10MethodDecl) {
            return null;  // @NoInline annotation means something else
        }
        if (node instanceof Call) {
            return null; // will handle @NoInline annotation seperately
        }
        if (node instanceof ClassDecl) {
            if ( !((X10Ext) node.ext()).annotationMatching(NativeClassType).isEmpty() ||
                 !((X10Ext) node.ext()).annotationMatching(NativeRepType).isEmpty() ) {
               debug("Native Class/Rep: short-circuiting inlining for children of " +node, node);
               return node;
            }   
        }
        if (ExpressionFlattener.cannotFlatten(node)) {
            debug("Cannot flatten: short-circuiting inlining for children of " +node, node);
            return node; // don't inline inside Nodes that cannot be Flattened
        }
        if (node.ext() instanceof X10Ext) {
            if (!((X10Ext) node.ext()).annotationMatching(NoInlineType).isEmpty()) { // short-circuit inlining decisions
                debug("Explicit annotation: short-circuiting inlining for children of " +node, node);
                return node;
            }
        }
        return null;
    }
    
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Node result = n;
        if (!ALLOW_STMTEXPR) return n;  // FIXME: for now
        if (n instanceof X10Call) {
            result = inlineMethodCall((X10Call_c) n);
        } else if (n instanceof ClosureCall) {
            result = inlineClosureCall((ClosureCall) n);
        } else if (n instanceof X10MethodDecl) {
            return nonInlineOnlyMethods((X10MethodDecl) n);
        } else {
            return result;
        }
        if (n != result) {
            if (VERBOSE) {
                Warnings.issue(job, "___ Inlining: " +n, n.position());
            }
        } else {
            if (VERY_VERBOSE || inliningRequired) {
                Warnings.issue(job, "NOT Inlining: " +n+ " (because " +reason+ ")", n.position());
            }
        }
        return result;
    }

    /**
     * @param n
     * @return
     */
    private Node nonInlineOnlyMethods(X10MethodDecl method) {
        if (((X10MethodDef) method.methodDef()).annotationsMatching(InlineOnlyType).isEmpty()) 
            return method;
        return null;
    }

    private Expr inlineMethodCall(X10Call_c call) {
        if (null == InlineType) return call;
        beginSpeculativeCompile();
        Expr result = call;
        X10MethodDecl decl = getInlineDecl(call);
        if (null != decl) {
            String methodId = getMethodId(decl);
            decl = instantiate(decl, call);
            // TODO: handle "this" parameter like formals (cache actual in temp, assign to local (transformed this)
            LocalDecl thisArg  = createThisArg(call);
            LocalDecl thisForm = createThisFormal((X10MethodInstance) call.methodInstance(), thisArg);
            decl = normalizeMethod(decl, thisForm); // Ensure that the last statement of the body is the only return in the method
            result = rewriteInlinedBody(call.position(), decl.returnType().type(), decl.formals(), decl.body(), thisArg, thisForm, call.arguments());
            result = (Expr) result.visit(new AlphaRenamer());
            if (-1 == inlineInstances.search(methodId)) {     // non recursive inlining of the inlined body
                inlineInstances.push(methodId);
                result = (Expr) result.visit(this);
                inlineInstances.pop();
            } else {
                recursionDepth[0]++;
                if (recursionDepth[0] <= RECURSION_DEPTH_LIMIT) { // recursive inlining of the inlined body
                    result = (Expr) result.visit(this);
                }
                recursionDepth[0]--;
            }
            if (0 == inlineInstances.size()) { // don't propagate constants too early
                result = (Expr) propagateConstants(result);
            }
            // TODO: tell context that the place of result is the same as that of its surrounding context
        }
        if (endSpeculativeCompileWithErrors()) {
            if (VERBOSE) Warnings.issue(job, "INFORMATION inlining aborted due to Errors for " +call, call.position());
            return call; // DO NOT INLINE! // this may be overly conservative (at least in some cases)
        }
        return result;
    }

    private Goal.Status savedState;
    private ErrorQueue  savedQueue;
    /**
     * Don't let fatal Errors in speculative compilation terminate the containing compile.
     * Speculative compilation ends with a call to endSpeculativeCompileWithErrors().  If
     * that method is not called, mayhem will insue!
     */
    private void beginSpeculativeCompile() {
        savedState = job.extensionInfo().scheduler().currentGoal().state();
        // savedQueue = job.compiler().swapErrorQueue(new SimpleErrorQueue());
        savedQueue = job.compiler().swapErrorQueue(new SilentErrorQueue(1024, null));
    }
    /**
     * Terminate a speculative compilation initiated by beginSpeculativeCompile().
     * 
     * @return true is the speculative compilation produced what would have been fatal Errors
     */
    private boolean endSpeculativeCompileWithErrors() {
        ErrorQueue speculativeQueue = job.compiler().swapErrorQueue(savedQueue);
        if (0 < speculativeQueue.errorCount()) {
            job.extensionInfo().scheduler().clearFailed();
            job.extensionInfo().scheduler().currentGoal().update(savedState);
            if (speculativeQueue instanceof SilentErrorQueue) {
                try {
                    System.err.flush();
                    java.lang.Thread.sleep(1);
                    System.out.println("The following errors were ignored during speculative compilation:");
                    for (Object e :  ((SilentErrorQueue) speculativeQueue).getErrors()) {
                        System.out.println("  " + e);
                    }
                    System.out.flush();
                    java.lang.Thread.sleep(1);
                } catch (InterruptedException e1) {
                }
            } else {
                speculativeQueue.flush();
            }
            return true;
        }
        return false;
    }

    /**
     * @param decl
     * @return
     */
    private String getMethodId(X10MethodDecl decl) {
        // String methodId = decl.methodDef().toString();
        // String methodId = getContainer((X10MethodDef) decl.methodDef()).fullName()+ "." +decl.methodDef().signature();
        String methodId = getContainer((X10MethodDef) decl.methodDef()).fullName()+ "." +decl.name()+ "("; 
        String c = "";
        for (Formal f : decl.formals()) {
            methodId += c+f.type().nameString();
            c = ",";
        }
        methodId += "):" + decl.returnType().nameString();
        return methodId;
    }

    private Expr inlineClosureCall(ClosureCall c) {
        Closure lit = getInlineClosure(c);
        if (null == lit) return c;
        List<Expr> args = new ArrayList<Expr>();
        int i = 0;
        for (Expr a : c.arguments()) {
            args.add(createCast(a.position(), a, c.closureInstance().formalTypes().get(i++)));
        }
        lit = normalizeClosure(lit); // Ensure that the last statement of the body is the only return in the closure
        Expr result = rewriteInlinedBody(c.position(), lit.returnType().type(), lit.formals(), lit.body(), null, null, args);
        result = (Expr) result.visit(new AlphaRenamer());
        result = (Expr) result.visit(this);
        result = (Expr) propagateConstants(result);
        return result;
    }

    /**
     * Get the declaration corresponding to a call to be inlined.
     * 
     * @param call
     * @return the declaration of the method invoked by call, or null if 
     *    the declaration cannot be found, or
     *    the call should not be inlined
     */
    private X10MethodDecl getInlineDecl(X10Call_c call) {
        debug("Inline " +call+ " ?", call);
        
        // get inline candidate
        X10MethodDef candidate = ((X10MethodInstance) call.methodInstance()).x10Def();
        if (annotationsPreventInlining(call)) {
            reason = "of annotation at call site";
            debug("Inlining failed because " + reason, call);
            return null;
        }
        
        // require inlining if either the call of the candidate are so annotated
        inliningRequired = annotationsRequireInlining(call, candidate);
        
        // unless required, skip candidates previously found to be uninlinable
        if (!inliningRequired && dontInline.contains(candidate)) {
            reason = "of previous decision for candidate: " + candidate;
            debug("Inlining failed because " + reason, call);
            return null;
        }
        
        // unless required, don't inline if the candidate annotations prevent it
        if (!inliningRequired && annotationsPreventInlining(candidate)) {
            reason = "of annotation on candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            return null;
        }
        
        // see if the declaration for this candidate has already been cached
        X10MethodDecl decl = def2decl.get(candidate);
        if (null != decl) {
            return decl;
        }
        
        // get container and declaration for inline candidate
        X10ClassDef container = getContainer(candidate);
        if (null == container) {
            reason = "unable to find container for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            return null;
        }
        if (isVirtualOrNative(candidate, container)) {
            reason = "call is virtual or native";
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            return null;
        }
        Job candidateJob = getJob(candidate, container);
        if (null == candidateJob) {
            reason = "unable to find job for candidated: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            badJobs.add(candidateJob);
            return null;
        }
        if (annotationsPreventInlining(container)) {
            reason = "of Native Class/Rep annotation of container: " +container;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            badJobs.add(candidateJob);
        }
        
        Node ast = candidateJob.ast();
        if (null == ast) {
            reason = "unable to find ast for candidated: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            badJobs.add(candidateJob);
            return null;
        }
        
        decl = getDeclaration(candidate, ast);
        if (null == decl) {
            reason = "unable to find declaration for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            return null;
        }
        
        if (this.job.compiler().errorQueue().hasErrors()) { // This may be overly conservative
            reason = "there werer errors compiling candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            dontInline.add(candidate);
            badJobs.add(candidateJob);
            return null;
        }
        
        if (!inliningRequired ) { // decide whether to inline candidate
            if (x10.Configuration.INLINE_SMALL_METHODS) {
                int cost = getCost(decl, candidateJob);
                if (SMALL_METHOD_MAX_SIZE < cost ) {
                    reason = "of excessive cost, " + cost;
                    debug("Inlining failed because " + reason, call);
                    dontInline.add(candidate);
                    return null;
                } else {
                    // debug("Inlining small method with cost " + cost, call);
                }
            } else {
                reason = "inlining not explicitly required";
                debug("Inlining failed because " + reason, call);
                dontInline.add(candidate);
                return null;
            }
        }
        // remember what to inline this candidate with if we ever see it again
        def2decl.put(candidate, decl);
        return decl;
    }

    /**
     * @param container
     * @return
     */
    private boolean annotationsPreventInlining(X10ClassDef container) {
      if (!container.annotationsMatching(NativeRepType).isEmpty()) return true;
      if (!container.annotationsMatching(NativeClassType).isEmpty()) return true;
      return false;
    }

    /**
     * @param call
     * @return
     */
    private boolean annotationsPreventInlining(X10Call_c call) {
        if (!((X10Ext) call.ext()).annotationMatching(NoInlineType).isEmpty()) return true;
        return false;
    }

    /**
     * @param candidate
     * @return
     */
    private boolean annotationsPreventInlining(X10MethodDef candidate) {
        if (!candidate.annotationsMatching(NoInlineType).isEmpty()) return true;
        if (!candidate.annotationsMatching(NativeType).isEmpty()) return true;
 //     if (!candidate.annotationsMatching(NativeRepType).isEmpty()) return true;
        return false;
    }

    /**
     * @param call
     * @param candidate
     * @return
     */
    private boolean annotationsRequireInlining(X10Call_c call, X10MethodDef candidate) {
        if (!((X10Ext) call.ext()).annotationMatching(InlineType).isEmpty()) return true;
        if (!candidate.annotationsMatching(InlineType).isEmpty()) return true;
        return false;
    }

    /**
     * Get the definition of the X10 Class that implements a given method.
     * 
     * @param candidate the method definition whose container is desired
     * @return the definition of the X10 Class containing md
     */
    private X10ClassDef getContainer(X10MethodDef candidate) {
        Ref<? extends StructType> containerRef = candidate.container();
        StructType containerType = Types.get(containerRef);
        Type containerBase = X10TypeMixin.baseType(containerType);
        assert (containerBase instanceof X10ClassType);
        X10ClassDef container = ((X10ClassType) containerBase).x10Def();
        return container;
    }

    /**
     * Check that a candidate method is eligible to be inlined.
     * 
     * @param candidate the method conside red for inlining
     * @param container the class containing the candidate
     * @return true if the method obviously should not be inlined, false otherwise
     */
    private boolean isVirtualOrNative(X10MethodDef candidate, X10ClassDef container) {
        Flags mflags = candidate.flags();
        Flags cflags = container.flags();
        if (!mflags.isFinal() && !mflags.isPrivate() && ! mflags.isStatic() && !cflags.isFinal() && !container.isStruct()) 
            return true;
        if (mflags.isNative() || cflags.isNative()) 
            return true;
        return false;
    }

    /**
     * Obtain the job for containing the declaration for a given method.
     * Run the preliminary compilation phases on the job's AST.
     * 
     * Note Errors during speculative compilation should not be fatal.
     * The mechanism implementing this behavior consists of a pair of hacks that should be fixed.
     * 
     * @param candidate 
     * @param container
     * @return
     */
    private Job getJob(X10MethodDef candidate, X10ClassDef container) {
        Position pos = candidate.position();
        Job job      = container.job();
        debug("Looking for job: " +job, null);
        try {
            /*
            if (null == job) {
                // reconstruct job from position
                String file = pos.file();
                String path = pos.path();
                Source source = new Source(file, path, null);
                job = xts.extensionInfo().scheduler().addJob(source);
            }
            */
            if (null == job) {
                Warnings.issue(job, "Unable to find or create job for method: " +candidate, pos);
                return null;
            } else if (badJobs.contains(job)) {
                return null;
            } else if (job != this.job()) {
                String key = container.fullName().toString().intern();
                // String key = job.toString();
                Node ast = astMap.get(key);
                if (null == ast) {
                    if (null == job.ast()) {
                        badJobs.add(job);
                        return null;
                    }
                    // TODO reconstruct the AST for the job will all preliminary compiler passes
                    ast = job.ast().visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin());
                    if (null == ast) {
                        debug("Unable to reconstruct AST for " + job, null);
                        badJobs.add(job);
                        return null;
                    }
                    debug("Reconstructed AST for " + job, null);
                    job.ast(ast);
                    astMap.put(key, ast);
                }
                // job.ast(ast);
            }
        } catch (Exception x) {
            String msg = "  AST for job, " +job+ " (for candidate " +candidate+ "), does not typecheck (" +x+ ")";
            SemanticException e = new SemanticException(msg, candidate.position());
            badJobs.add(job);
            Errors.issue(job, e);
            // x.printStackTrace();
            return null;
        }
        return job;
    }

    /**
     * Walk an AST looking for the declaration of a given method.
     * 
     * @param candidate the method whose declaration is desired
     * @param ast the abstract syntax tree containing the declaration
     * @return the declaration for the indicated method, or null if no declaration can be found or it has an empty body.
     */
    private X10MethodDecl getDeclaration(final X10MethodDef candidate, final Node ast) {
    final Position pos         = candidate.position();
    final X10MethodDecl[] decl = new X10MethodDecl[1];
    ast.visit(new NodeVisitor() { // find the declaration of md
        public Node override(Node n) {
            if (null != decl[0]) 
                return n;  // we've already found the decl, short-circuit search
            if (n instanceof TypeNode) 
                return n;  // TypeNodes don't contain decls, short-circuit search
            if (!pos.isCompilerGenerated() && !contains(n.position(), pos))
                return n;  // definition of md isn't inside n, short-circuit search
            if (n instanceof X10MethodDecl && candidate == ((X10MethodDecl) n).methodDef()) {
                decl[0] = (X10MethodDecl) n;
                return n;  // we found the decl for the candidate, short-circuit search
            }
            return null;   // look for the decl inside n
        }
    });
    if (null == decl[0]) {
        Warnings.issue(job, "Declaration not found for " +candidate, pos);
        return null;
    }
    if (null == decl[0].body()) {
        Warnings.issue(job, "No declaration body for " +decl[0]+ " (" +candidate+ ")", pos);
        return null;
    }
    return decl[0];
}

/**
 * @param decl
 * @param job
 * @return
 */
private int getCost(X10MethodDecl decl, Job job) {
    int cost = ice.getCost(decl, job);
    return cost;
}

    /**
     * @param c
     * @return
     */
    private Closure getInlineClosure(ClosureCall c) {
        return getClosureLiteral(c.target());
    }

    private Closure getClosureLiteral(Expr target) {
        if (target instanceof Closure)
            return (Closure) target;
        if (target instanceof ParExpr)
            return getClosureLiteral(((ParExpr)target).expr());
        // TODO  Inline Locals (and field instances?) that have literal closure values
        return null;
    }

    /**
     * @param decl
     * @param ths
     * @return
     */
    private X10MethodDecl normalizeMethod(X10MethodDecl decl, LocalDecl thisFormal) {
        LocalDef thisDef = null == thisFormal ? null : thisFormal.localDef();
        return (X10MethodDecl) decl.visit(new InliningRewriter(decl, thisDef));
    }

    /**
     * @param lit
     * @return
     */
    private Closure normalizeClosure(Closure lit) {
        return (Closure) lit.visit(new InliningRewriter(lit));
    }

    public Node propagateConstants(Node n) {
        ConstantPropagator cp = new ConstantPropagator(job, ts, nf);
        cp = (ConstantPropagator) cp.context(context());
        return n.visit(cp);
    }

    /**
     * Create a cast (explicit conversion) expression.
     * 
     * @param pos the Position of the cast in the source code
     * @param expr the Expr being cast
     * @param toType the resultant type
     * @return the synthesized Cast expression (expr as toType), or 
     *         the original expression (if the cast is unnecessary)
     * TODO: move into Synthesizer
     */
    public Expr createCast(Position pos, Expr expr, Type toType) {
        if (xts.typeDeepBaseEquals(expr.type(), toType, context())) return expr;
       return xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, toType), expr, Converter.ConversionType.UNCHECKED ).type(toType);
    }

    private LocalDecl createThisArg(X10Call c) {
        if (!(c.target() instanceof Expr)) return null;
        Expr target = (Expr) c.target();
        if (target instanceof Special && ((Special) target).kind() == Special.SUPER) {
            target = rewriteSuperAsThis((Special) target);
        }
        LocalDef def  = xts.localDef(c.target().position(), xts.Final(), Types.ref(c.target().type()), Name.make("target"));
        LocalDecl ths = syn.createLocalDecl(c.target().position(), def, target);
        return ths;
    }

    /*
     * In general, when the body of one method is inlined into the body of another,
     * the keywords "this" and "super" loose their meanings.  InlineRewriter deals
     * with the case of "this".  It complains, if it encounters "super".  Rewriting
     * "super" as "(this as ST)" won't work because we loose the fact that the call
     * is non-virtual. 
     * 
     * However, this rewrite can be used to handle the "this parameter" when inlining
     * calls of the form "super.foo()" (because the method instance has already been
     * resolved).  (Java does not allow the bare keyword "super" to occur where an 
     * expression is required.  It does, of course, allow "this" to be so used.  It just
     * needs to be coersed to the right type.)
     */
    private Special rewriteSuperAsThis(Special special) {
        assert (special.kind() == Special.SUPER) : "Unexpected special kind: " + special;
        Special result = xnf.Special(special.position(), Special.THIS, special.qualifier());
        result = (Special) result.type(special.type());
        return result;
    }

    private LocalDecl createThisFormal(X10MethodInstance mi, LocalDecl init) {
        if (mi.flags().isStatic()) return null;
        TypeParamSubst typeMap = makeTypeMap(mi);
        Type thisType = typeMap.reinstantiate(mi.def().container().get());
        Expr expr = null == init ? null : createCast(init.position(), syn.createLocal(init.position(), init), thisType);
        LocalDecl thisDecl = syn.createLocalDecl(mi.position(), Flags.FINAL, Name.make("this"), expr);
        return thisDecl;
    }
    
    private LocalDef computeThis(MethodDef def) {
//        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        if (def.flags().isStatic()) return null;
        return xts.localDef(def.position(), xts.Final(), def.container(), Name.makeFresh("this"));
    }

    // TODO: move this to Position
    private static boolean contains(Position outer, Position inner) {
        if (!outer.file().equals(inner.file())) return false;
        if (!outer.path().equals(inner.path())) return false;
        return (outer.offset() <= inner.offset() && inner.endOffset() <= inner.endOffset());
    }

    private X10MethodDecl instantiate(final X10MethodDecl decl, X10Call c) {
        debug("Instantiate " +decl, c);
        final X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
        TypeParamSubst typeMap = makeTypeMap(mi);
        return (X10MethodDecl) decl.visit(new TypeTransformingVisitor(job, ts, nf, typeMap).context(context()));
    }

    public static class TypeTransformingVisitor extends X10LocalClassRemover.TypeTransformingVisitor {
        protected TypeTransformingVisitor(Job job, TypeSystem ts, NodeFactory nf, TypeParamSubst subst) {
            super(job, ts, nf, subst);
        }

        @Override
        protected TypeParamNode transform(TypeParamNode pn, TypeParamNode old) {
            return pn;
        }

        @Override
        protected Field transform(Field f, Field old) {
            f = f.targetImplicit(false);
            return super.transform(f, old);
        }

        @Override
        protected Call transform(Call c, Call old) {
            c = c.targetImplicit(false);
            return super.transform(c, old);
        }

        @Override
        protected Special transform(Special s, Special old) {
            if (s.kind().equals(Special.SUPER)) {
                assert (false) : "Not yet implemented, can't instantiate " +s;
            }
            return super.transform(s, old);
        }

        @Override
        protected X10ClassDecl transform(X10ClassDecl d, X10ClassDecl old) {
            boolean sigChanged = d.superClass() != old.superClass();
            List<TypeNode> interfaces = d.interfaces();
            List<TypeNode> oldInterfaces = old.interfaces();
            for (int i = 0; i < interfaces.size(); i++) {
                sigChanged |= interfaces.get(i) != oldInterfaces.get(i);
            }
            if (sigChanged) {
                throw new InternalCompilerError("Inlining of code with instantiated local classes not supported");
            }
            return d;
        }

        @Override
        protected X10FieldDecl transform(X10FieldDecl d, X10FieldDecl old) {
            assert (false) : "Not yet implemented, can't instantiate " +d;
            return d;
        }

        @Override
        protected X10ConstructorDecl transform(X10ConstructorDecl d, X10ConstructorDecl old) {
            assert (false) : "Not yet implemented, can't instantiate " +d;
            return d;
        }

        @Override
        protected X10MethodDecl transform(X10MethodDecl d, X10MethodDecl old) {
            boolean sigChanged = d.returnType() != old.returnType();
            List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
            List<LocalDef> formalNames = new ArrayList<LocalDef>();
            List<Formal> params = d.formals();
            List<Formal> oldParams = old.formals();
            for (int i = 0; i < params.size(); i++) {
                Formal p = params.get(i);
                sigChanged |= p != oldParams.get(i);
                argTypes.add(p.type().typeRef());
                formalNames.add(p.localDef());
            }
            sigChanged |= d.guard() != old.guard();
            List<Ref <? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
            SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(typeSystem()) : d.exceptions();
            SubtypeSet oldExcs = old.exceptions();
            if (null != excs) {
                for (Type et : excs) {
                    sigChanged |= !oldExcs.contains(et);
                    excTypes.add(Types.ref(et));
                }
            }
            sigChanged |= d.offerType() != old.offerType();
            if (sigChanged) {
                X10MethodDef md = (X10MethodDef) d.methodDef();
                DepParameterExpr g = d.guard();
                TypeNode ot = d.offerType();
                X10TypeSystem xts = (X10TypeSystem) ts;
                X10MethodDef imd = xts.methodDef(md.position(), md.container(), md.flags(), d.returnType().typeRef(),
                                                 md.name(), md.typeParameters(), argTypes, md.thisVar(), formalNames,
                                                 g == null ? null : g.valueConstraint(),
                                                 g == null ? null : g.typeConstraint(), 
                                                 ot == null ? null : ot.typeRef(), null /* the body will never be used */);
                return d.methodDef(imd);
            }
            return d;
        }
    }

    /**
     * @param decl
     * @return
     */
    private TypeParamSubst makeTypeMap(X10MethodInstance method) {
        List<Type>          typeArgs  = new ArrayList<Type>();
        List<ParameterType> typeParms = new ArrayList<ParameterType>();
        typeArgs.addAll(method.typeParameters());
        typeArgs.addAll(((X10ClassType) method.container()).typeArguments());
        typeParms.addAll(method.x10Def().typeParameters());
        typeParms.addAll(((X10ClassType) method.container()).x10Def().typeParameters());
        return new TypeParamSubst(xts, typeArgs, typeParms);
    }

    // TODO: generate a closure call instead of a statement expression // is this still necessary?
    private Expr rewriteInlinedBody(Position pos, Type retType, List<Formal> formals, Block body, LocalDecl thisArg, LocalDecl thisFormal, List<Expr> args) {
        
        // Create statement expression from body.
        // body is in normal form:
        //   1) last statement in body is a return statement, and
        //   2) this is the only return in the body.
        List<Stmt> bodyStmts = body.statements();
        assert (bodyStmts.get(bodyStmts.size()-1) instanceof Return) : "Last statement is not a return";
        Return ret = (Return) bodyStmts.get(bodyStmts.size()-1);
        List<Stmt> statements = new ArrayList<Stmt>();
        for (Stmt stmt : bodyStmts) {
            if (stmt != ret) {
                statements.add(stmt);
            }
        }
        Expr e = ret.expr();
        if (null != e)
            e = createCast(e.position(), e, retType);
        StmtExpr result = syn.createStmtExpr(pos, statements, e);

        // create declarations to prepend to result
        List<Stmt> declarations = new ArrayList<Stmt>();
        if (thisArg != null) { // declare temporary for "this" arg, if call isn't static
            declarations.add(thisArg);
        }
        // initialize temporaries to args
        LocalDecl[] temps = new LocalDecl[args.size()];
        for (int i=0; i<temps.length; i++) {
            Expr a = args.get(i);
            temps[i] = syn.createLocalDecl(a.position(), Flags.FINAL, Name.makeFresh(), a);
            tieLocalDefToItself(temps[i].localDef());
            declarations.add(temps[i]);
        }
        if (thisFormal != null) { // declare local for "this" parameter, if method isn't static
            declarations.add(thisFormal);
        }
        // initialize new locals (transformed formals) to temporaries (args)
        for (int i=0; i<temps.length; i++) {
            LocalDecl temp   = temps[i];
            X10Formal formal = (X10Formal) formals.get(i);
            Expr value       = createCast(temp.position(), syn.createLocal(temp.position(), temp), formal.type().type());
            LocalDecl local  = syn.transformFormalToLocalDecl(formal, value); 
            tieLocalDefToItself(local.localDef());
            tieLocalDef(local.localDef(), temp.localDef());
            declarations.add(local);
        }

        result = result.prepend(declarations);
        return result;
    }

    /**
     * @param d
     */
    private void tieLocalDefToItself(LocalDef d) {
        tieLocalDef(d, d);
    }

    private void tieLocalDef(LocalDef d, LocalDef o) {
        Type type = Types.get(d.type());
        try {
            type = X10TypeMixin.addSelfBinding(type, XTerms.makeLocal(XTerms.makeName(o, o.name().toString())) );
        } catch (XFailure e) {
        }
        ((Ref<Type>)d.type()).update(type);
    }

    /**
     * Rewrites a given closure so that it has exactly one return statement at the end.
     * Also, replaces "this" parameter by a local variable.
     * @author igor
     * TODO: factor out into its own class
     */
    public class InliningRewriter extends ContextVisitor {
        private final FunctionDef def;
        private final LocalDef ths;
        private final LocalDef ret;
        private final Name label;
        private boolean hasReturn;
        public InliningRewriter(Closure closure) {
            this(closure.closureDef(), null, closure.body().statements());
        }
        public InliningRewriter(X10MethodDecl decl, LocalDef ths) {
            this(decl.methodDef(), ths, decl.body().statements());
        }
        private InliningRewriter(FunctionDef def, LocalDef ths, List<Stmt> body) {
            super(Inliner.this.job(), Inliner.this.typeSystem(), Inliner.this.nodeFactory());
            this.context = Inliner.this.context();
            this.def = def;
            this.ths = ths;
            this.hasReturn = false;
            if (body.size() == 1 && body.get(0) instanceof Return) {
                // Closure already has the right properties; make return rewriting a no-op
                this.ret = null;
                this.label = null;
            } else {
                X10TypeSystem xts = (X10TypeSystem) typeSystem();
                Name rn = Name.makeFresh("ret");
                Type rt = def.returnType().get();
                this.ret = rt.isVoid() ? null : xts.localDef(def.position(), xts.NoFlags(), Types.ref(rt), rn);
                this.label = Name.makeFresh("__ret");
            }
        }
        public Node override(Node n) {
            if (def == null) return n;
            return null;
        }
        // TODO: use override to short-circuit the traversal
        public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            if (n instanceof AmbExpr || n instanceof AmbAssign || n instanceof AmbTypeNode) {
                throw new InternalCompilerError("Ambiguous node found: "+n, n.position());
            }
            if (n instanceof X10MethodDecl)
                return visitMethodDecl((X10MethodDecl)n);
            if (n instanceof Closure)
                return visitClosure((Closure)n);
            if (n instanceof Return)
                return visitReturn((Return)n);
            if (n instanceof Field)
                return visitField((Field)n);
            if (n instanceof Call)
                return visitCall((X10Call)n);
            if (n instanceof Special)
                return visitSpecial((Special)n);
            return n;
        }
        private Block rewriteBody(Position pos, Block body) throws SemanticException {
            hasReturn = true;
            if (label == null ||
                !hasReturn) 
               return body;
            X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
            X10TypeSystem xts = (X10TypeSystem) typeSystem();
            List<Stmt> newBody = new ArrayList<Stmt>();
            if (ret != null && hasReturn) {
                newBody.add(xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                            xnf.CanonicalTypeNode(pos, ret.type()),
                            xnf.Id(pos, ret.name())).localDef(ret));
            }
            newBody.add(xnf.Labeled( pos, 
                                     xnf.Id(pos, label),
                                     xnf.Do(pos, body, syn.createFalse(pos)) )); 
            if (ret != null && hasReturn) {
                Expr rval = xnf.Local(pos, xnf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(ret.type().get());
                newBody.add(xnf.Return(pos, rval));
            } else {
                newBody.add(xnf.Return(pos));
            }
            return xnf.Block(body.position(), newBody);
        }
        // def m(`x:`T):R=S -> def m(`x:`T)={r:R; L:do{ S[return v/r=v; break L;]; }while(false); return r;}
        private X10MethodDecl visitMethodDecl(X10MethodDecl n) throws SemanticException {
            // First check that we are within the right method
            if (n.methodDef() != def) return n;
            return (X10MethodDecl) n.body(rewriteBody(n.position(), n.body()));
        }
        // (`x:`T):R=>S -> (`x:`T)=>{r:R; L:do{ S[return v/r=v; break L;]; }while(false); return r;}
        private Closure visitClosure(Closure n) throws SemanticException {
            // First check that we are within the right closure
            if (n.closureDef() != def) return n;
            return (Closure) n.body(rewriteBody(n.position(), n.body()));
        }
        // return v; -> r=v; break L;
        private Stmt visitReturn(Return n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (label == null) return n;
            assert ((ret == null) == (n.expr() == null));
            this.hasReturn = true;
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            List<Stmt> retSeq = new ArrayList<Stmt>();
            if (ret != null) {
                Type rt = ret.type().get();
                Expr xl = xnf.Local(pos, xnf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(rt);
                retSeq.add(xnf.Eval(pos, xnf.Assign(pos, xl, Assign.ASSIGN, n.expr()).type(rt)));
            }
            retSeq.add(xnf.Break(pos, xnf.Id(pos, label)));
            return xnf.StmtSeq(pos, retSeq);
        }
        private Expr getThis(Position pos) {
            X10NodeFactory xnf = (X10NodeFactory) nf;
            LocalInstance tli = ths.asInstance();
            return xnf.Local(pos, xnf.Id(pos, tli.name())).localInstance(tli).type(tli.type());
        }
        // f -> ths.f
        private Field visitField(Field n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (!n.isTargetImplicit()) return n;
            FieldInstance fi = n.fieldInstance();
            assert ((ths == null) == (fi.flags().isStatic()));
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            if (fi.flags().isStatic()) {
                return n.target(xnf.CanonicalTypeNode(pos, fi.container())).targetImplicit(false);
            }
            return n.target(getThis(pos)).targetImplicit(false);
        }
        // m(...) -> ths.m(...)
        private X10Call visitCall(X10Call n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (!n.isTargetImplicit()) return n;
            MethodInstance mi = n.methodInstance();
            assert ((ths == null) == (mi.flags().isStatic()));
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            if (mi.flags().isStatic()) {
                return (X10Call) n.target(xnf.CanonicalTypeNode(pos, mi.container())).targetImplicit(false);
            }
            return (X10Call) n.target(getThis(pos)).targetImplicit(false);
        }
        // this -> ths
        private Expr visitSpecial(Special n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (n.kind() == Special.SUPER) {
                throw new InternalCompilerError("super not supported when inlining", n.position());
            }
            if (n.kind() == X10Special.SELF) {
                return n;
            }
            assert (n.kind() == Special.THIS);
            Position pos = n.position();
            return getThis(pos);
        }
    }

    private class InlineCostEstimator extends X10DelegatingVisitor {
        private static final int NATIVE_CODE_COST = 989898;
        int            cost;
        X10TypeSystem  xts;
        X10NodeFactory xnf;
        InlineCostEstimator (X10TypeSystem ts, X10NodeFactory nf) {
            xts = ts;
            xnf = nf;
        }
        int getCost(Node n, Job job) {
            if (null == n) return 0;
            InlineCostVisitor visitor = new InlineCostVisitor(job, xts, xnf, this);
            cost = 0;
            n.visit(visitor);
            return cost;
        }
        public final void visit(Call_c c) {
            cost++;
            checkForNativeCode(c);
        }
        public final void visit(Node n) {
            checkForNativeCode(n);
        }
        /**
         * If a node has a Native annotation, then the call cannot be inlined.
         * For now, this gets expressed by setting the cost really big (NATIVE_COST).
         * 
         * @param n the node that might have a native implementation
         */
        private final void checkForNativeCode(Node n) {
            if (n.ext() instanceof X10Ext && (!((X10Ext) n.ext()).annotationMatching(NativeType).isEmpty())) {
                cost = NATIVE_CODE_COST;
            }
        }
    }
    
    private class InlineCostVisitor extends ErrorHandlingVisitor {
        InlineCostEstimator ice;
        /**
         * @param job
         * @param ts
         * @param nf
         */
        public InlineCostVisitor(Job job, TypeSystem ts, NodeFactory nf, InlineCostEstimator ce) {
            super(job, ts, nf);
            ice = ce;
        }
        /* (non-Javadoc)
         * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
         */
        @Override
        protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            ice.visitAppropriate(n);
            return n;
        }
    }

}
