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

package x10.optimizations.inlining;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CodeBlock;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.MemberInstance;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SilentErrorQueue;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.InlinableCall;
import x10.ast.ParExpr;
import x10.ast.StmtExpr;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10LocalDef;
import x10.types.X10MemberDef;
import x10.types.X10ParsedClassType;
import x10.types.checker.Converter;
import x10.types.constraints.CTerms;
import x10.util.AltSynthesizer;
import x10.visit.ConstantPropagator;
import x10.visit.ConstructorSplitterVisitor;
import x10.visit.Desugarer;
import x10.visit.ExpressionFlattener;
import x10.visit.IfdefVisitor;
import x10.visit.NodeTransformingVisitor;
import x10.visit.X10DelegatingVisitor;
import x10.visit.X10TypeChecker;

/**
 * This visitor inlines calls to methods and closures under the following
 * conditions:
 * <ul>
 * <li>The exact class of the method target is known.
 * <li>The method being invoked is annotated @X10.compiler.Inline or the method
 * is found to be "small".
 * <li>The closure call target is a literal closure.
 * </ul>
 * 
 * @author nystrom
 * @author igor
 * @author Bowen Alpern (alpernb@us.ibm.com)
 */
@SuppressWarnings("unchecked")
public class Inliner extends ContextVisitor {
    
    static private final boolean ALLOW_NON_VIRTUAL_CALLS = false;

    private final boolean INLINE_CONSTANTS;
    private final boolean INLINE_METHODS;
    private final boolean INLINE_CLOSURES;
    private final boolean INLINE_IMPLICIT;
    private final boolean INLINE_CONSTRUCTORS;
    private final boolean INLINE_STRUCT_CONSTRUCTORS;
    private final boolean INLINE_GENERIC_CONSTRUCTORS;
    private final boolean EXPERIMENTAL;
    
    static final boolean DEBUG = false;
//  private static final boolean DEBUG = true;

    private static final int VERBOSITY = 0;

    /**
     * The size of the largest method to be considered small enough to be inlined implicitly, if
     * implicit inlining is enabled.
     */
    private final int implicitMax;

    /**
     * 
     */
    private AltSynthesizer syn;
    private InlineCostEstimator ice;
    private polyglot.frontend.Compiler compiler;

    public Inliner(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        ExtensionInfo extInfo = (ExtensionInfo) job.extensionInfo();
        compiler = extInfo.compiler();
        Configuration config = ((X10CompilerOptions) extInfo.getOptions()).x10_config;
        EXPERIMENTAL     = config.EXPERIMENTAL;
        INLINE_CONSTANTS = config.OPTIMIZE && config.INLINE_CONSTANTS;
        INLINE_METHODS   = config.OPTIMIZE && config.INLINE_METHODS;
        INLINE_CLOSURES  = config.OPTIMIZE && config.INLINE_CLOSURES;
        INLINE_CONSTRUCTORS         = x10.optimizations.Optimizer.CONSTRUCTOR_SPLITTING(extInfo) && config.INLINE_CONSTRUCTORS;
        INLINE_STRUCT_CONSTRUCTORS  = config.INLINE_STRUCT_CONSTRUCTORS; // pretend the constructor for a struct is annotated @INLINE
        INLINE_GENERIC_CONSTRUCTORS = true;
        INLINE_IMPLICIT    = EXPERIMENTAL && config.OPTIMIZE && config.INLINE_METHODS_IMPLICIT;
        INLINE_DEPTH_LIMIT = INLINE_IMPLICIT ? 100 : 100;
 //     implicitMax        = EXPERIMENTAL ? 1 : 0;
        implicitMax        = 0;
    }

    /**
     * Explicit inlining (via annotations) and inlining of small methods (if
     * enabled) is done to an arbitrary depth for non-recursive calls. There is
     * a somewhat hoaky mechanism for limiting the number of recursive calls
     * that are inlined.
     * 
     * TODO: implement a space budget based policy mechanism for controling inlining. 
     */
    private final Stack<String> inlineInstances = new Stack<String>();
    private final int recursionDepth[] = new int[1];
    private final int INLINE_DEPTH_LIMIT;
    private static final int INITIAL_RECURSION_DEPTH = 0;
    private static final int RECURSION_DEPTH_LIMIT = 2;

    private List<String> reasons = new ArrayList<String>();
    private boolean inliningRequired; // move these (DEBUG)

    private long lastTime;
    private long timer () {
        long time = System.currentTimeMillis();
        long delta = time - lastTime;
        lastTime = time;
        return delta;
    }

    /**
     * @param msg
     * @return
     */
    private String report(String msg, Node n) {
        reasons.add(msg);
        if (DEBUG) debug("not inlining because " + msg, n);
        return msg;
    }

    static void debug (String msg, Node node) {
        debug(0L, msg, node);
    }
    
    
    private static void debug(long time, String msg, Node node) {
        try {
            Thread.sleep(1);
            System.out.print("DEBUG ");
            if (null != node)
                System.out.print(node.position() + ":  ");
            System.out.print(msg);
            if (0 < time) {
                System.out.print(" (" +time+ ")");
            }
            System.out.println();
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // Ignore exception (we are just trying to avoid stepping on writes to STDERR
        }
    }

    /**
     * Annotation types.
     */
    private Type ConstantType;
    private Type InlineType;
    private Type InlineOnlyType;
    private Type NoInlineType;
    private Type NativeType;
    private Type NativeRepType;
    private Type NativeClassType;

    /**
     * Names of the annotation types that pertain to inlining.
     */
    private static final QName CONSTANT_ANNOTATION     = QName.make("x10.compiler.CompileTimeConstant");
    private static final QName INLINE_ANNOTATION       = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION  = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION    = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");

    @Override
    public NodeVisitor begin() {
        syn = new AltSynthesizer(ts, nf);
        ice = new InlineCostEstimator(job, ts, nf);
        if (!x10.optimizations.Optimizer.INLINING((ExtensionInfo) job.extensionInfo())) {
            throw new InternalCompilerError("INLINING should not be being performed!");
        }
        recursionDepth[0] = INITIAL_RECURSION_DEPTH;
        try {
            ConstantType = ts.systemResolver().findOne(CONSTANT_ANNOTATION);
            InlineType = ts.systemResolver().findOne(INLINE_ANNOTATION);
            InlineOnlyType = ts.systemResolver().findOne(INLINE_ONLY_ANNOTATION);
            NoInlineType = ts.systemResolver().findOne(NO_INLINE_ANNOTATION);
            NativeType = ts.NativeType();
            NativeRepType = ts.NativeRep();
            NativeClassType = ts.systemResolver().findOne(NATIVE_CLASS_ANNOTATION);
        } catch (SemanticException e) {
            InternalCompilerError ice = new InternalCompilerError("Unable to find required Annotation Type");
            SemanticException se = new SemanticException(ice);		//TODO: internal compiler error, should be removed
            Errors.issue(job, se);
            InlineType = null;
            throw ice; // annotation types are required!
        }
        timer();
        return super.begin();
    }

    /**
     * Don't inline calls within a Node annotated "@NoInline".
     * 
     * @param node the Node possibly annotated
     * @return null, if node is to be walked for inlinable calls, or 
     *         node, if inlining should not be performed within it
     */
    public Node override(Node node) {
        Position pos = node.position(); // DEBUG
        if (null == InlineType) { // don't inline anything
            throw new InternalCompilerError("Inliner invoked without begin()");
        }
        if (2 <= VERBOSITY && node instanceof SourceFile) {
            Source s = ((SourceFile) node).source();
            Warnings.issue(this.job, "(begin inlining)", pos);
        }
        if (ExpressionFlattener.cannotFlatten(node, job)) { // TODO: check that flattening is actually required
            if (DEBUG) debug("Cannot flatten: short-circuiting inlining for children of " + node, node);
            return node; // don't inline inside Nodes that cannot be Flattened
        }
        if (node instanceof ConstructorCall && !INLINE_CONSTRUCTORS) {
            return node; // for now, don't flatten constructor calls
        }
        if (node instanceof X10MethodDecl) {
            return null; // @NoInline annotation means something else
        }
        if (node instanceof InlinableCall) {
            return null; // will handle @NoInline annotation seperately
        }
        if (node instanceof ClassDecl) { // Don't try to inline native classes
            if (!((X10Ext) node.ext()).annotationMatching(NativeClassType).isEmpty() || 
                !((X10Ext) node.ext()).annotationMatching(NativeRepType).isEmpty() ) {
                return node;
            }
        }
        if (node.ext() instanceof X10Ext) { // TODO: DEBUG only (remove this)
            if (!((X10Ext) node.ext()).annotationMatching(NoInlineType).isEmpty()) { // short-circuit inlining decisions
                if (DEBUG) debug("Explicit @NoInline annotation: short-circuiting inlining for children of " + node, node);
                return node;
            }
        }
        return null;
    }
    
    // returns true if @NoInline given on the node
    boolean hasNoInlineAnnotation (Node n) {
    	return !((X10Ext) n.ext()).annotationMatching(NoInlineType).isEmpty();
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Position pos = n.position();
        inliningRequired = false;
        reasons.clear();
        Node result = null;
        if (n instanceof ClosureCall && INLINE_CLOSURES) {
            if (4 <= VERBOSITY)
                Warnings.issue(job, "? inline level " +inlineInstances.size()+ " closure " +n, pos);
            result = inlineClosureCall((ClosureCall) n);
        } else if (n instanceof InlinableCall) {
            if (INLINE_CONSTANTS) {
                result = getCompileTimeConstant((InlinableCall) n);
                if (null != result) 
                    return result;
            }
            if (INLINE_METHODS && !hasNoInlineAnnotation(n)) {
                if (4 <= VERBOSITY)
                    Warnings.issue(job, "? inline level " +inlineInstances.size()+ " call " +n, pos);
                result = wrappedInlineMethodCall((InlinableCall) n);
            }
        } else if (n instanceof X10MethodDecl) {
            if (!((X10MethodDecl) n).methodDef().annotationsMatching(InlineOnlyType).isEmpty())
                return null; // ASK: is this the right way to remove a method decl from the ast?
            return n;
        } else {
            return n;
        }
        if (null == result) { // cannot inline this call
            if (5 <= VERBOSITY || inliningRequired) {
                String msg = "NOT Inlining: " +n+ " (level " +inlineInstances.size()+ ")";
                if (!reasons.isEmpty()) {
                    msg += "\n\tbecause ";
                    for (int i=0; i<reasons.size(); i++) {
                        msg += reasons.get(i);
                        if (i+2 < reasons.size())
                            msg += ", ";
                        else if (i+2 == reasons.size())
                            msg += ", and ";
                        else 
                            msg += ".";
                    }
                }
                Warnings.issue(job, msg, pos);
            }
            return n;
        }
        if (n != result) { // inlining this call
            if (n instanceof ConstructorCall && result instanceof StmtExpr) { // evaluate the expr // method calls in Stmt context are already sowrapped
                result = syn.createEval((StmtExpr) result);
            }
            if (3 <= VERBOSITY) {
                Warnings.issue(job, "INLINING: " +n+ " (level " +inlineInstances.size()+ ")", pos);
            }
        }
        return result;
    }

    /**
     * @param n
     * @return
     */
    private Node getCompileTimeConstant(InlinableCall call) {
        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) job().extensionInfo();
        x10Info.stats.startTiming("ConstantPropagator", "ConstantPropagator");
        try {
            X10Def def = (X10Def) getDef(call);
            List<Type> annotations = def.annotationsMatching(ConstantType);
            if (annotations.isEmpty()) return null;
            Expr arg = ((X10ClassType) annotations.get(0)).propertyInitializer(0);
            if (!arg.isConstant() || !arg.type().typeEquals(ts.String(), context)) 
                return null;
            String name = (String) arg.constantValue();
            Boolean negate = name.startsWith("!"); // hack to allow @CompileTimeConstant("!NO_CHECKS")
            if (negate) name = name.substring(1);
            X10CompilerOptions opts = (X10CompilerOptions) job.extensionInfo().getOptions();
            Object value = opts.x10_config.get(name);
            if (negate) 
                value = (Boolean) value ? false : true;
            Expr literal = new ConstantPropagator(job, ts, nf).toExpr(value, call.position());
            return literal;
        } catch (ConfigurationError e) {
            return null;
        } catch (OptionError e) {
            return null;
        }
        finally {
            x10Info.stats.stopTiming();
        }
    }

    private Expr inlineClosureCall(ClosureCall c) {
        Closure lit = getInlineClosure(c);
        if (null == lit) {
            report("of non literal closure call target " +c.target(), c);
            return null;
        }
        lit = (Closure) instantiate(lit, c);
        if (null == lit) {
            report("of failure to instatiate closure", c);
            return null;
        }
        InliningRewriter rewriter = new InliningRewriter(lit, job(), typeSystem(), nodeFactory(), context());
        lit = (Closure) lit.visit(rewriter); // Ensure that the last statement of the body is the only return in the closure
        if (null == lit) {
            report("of failure to normalize closure", c);
            return null;
        }
        if (null == lit.body()) {
            report("normalized closure has no body", c);
            return null;
        }
        lit = (Closure) lit.visit(new X10AlphaRenamer(this));
        List<Expr> args = new ArrayList<Expr>();
        int i = 0;
        for (Expr a : c.arguments()) {
            args.add(createCast(a.position(), a, c.closureInstance().formalTypes().get(i++)));
        }
        Expr result = rewriteInlinedBody(c.position(), lit.returnType().type(), lit.formals(), lit.body(), null, null, args);
        if (null == result) {
            report("of failure to rewrite closure body", c);
            return null;
        }
        result = (Expr) result.visit(this);
        result = (Expr) propagateConstants(result);
        return result;
    }

    private Expr inlineCall(InlinableCall call) {
        if (null == InlineType) {
            report("inlining disabled (no InlineType)", call);
            return null;
        }
        ProcedureDecl decl = getInlineDecl(call);
        if (null == decl) return null;
        List<AnnotationNode> annotations = ((X10Ext) decl.ext()).annotations();
        String sig = call.procedureInstance().signature();
        if (annotationsPreventInlining(decl) || annotationsPreventInlining(decl.procedureInstance())) {
            return null;
        }
        String signature = makeSignatureString(decl);
        decl = (ProcedureDecl) instantiate(decl, call);
        if (null == decl) {
            report("instantiation failure for " + signature, call);
            return null;
        }
        decl = (ProcedureDecl) decl.visit(new X10AlphaRenamer(this));
        LocalDecl thisArg = createThisArg(call);
        LocalDecl thisForm = null == thisArg ? null : createThisFormal(call, thisArg);
        LocalDef thisDef = null == thisForm ? null : thisForm.localDef();
        if (call instanceof ConstructorCall) {
            if (call.target() instanceof Local) { // the local was created by the compiler splitting a new, it's safe to use as this
                thisDef = ((Local) call.target()).localInstance().def();
                thisArg = null;
                thisForm = null;
            } else {
                Type type = ((ConstructorCall) call).constructorInstance().returnType();
                if (ts.isStruct(type)) {
                    // can't inline struct constructors with this as target
                    report("cannot inline constructor call with this target for struct " + type, call);
                    return null;
                }
            }
        }
        InliningRewriter rewriter = new InliningRewriter(decl, thisDef, job(), typeSystem(), nodeFactory(), context());
        decl = (ProcedureDecl) decl.visit(rewriter); // Ensure that the last statement of the body is the only return in the method
        if (null == decl || null == decl.body()) {
            return null;
        }
        Expr result = rewriteInlinedBody( call.position(), 
                                          decl instanceof MethodDecl ? ((MethodDecl) decl).returnType().type() : null, 
                                          decl.formals(), 
                                          decl.body(), 
                                          thisArg, 
                                          thisForm, 
                                          call.arguments() );
        if (null == result) {
            report("body doesn't contain a return (throw only)", call);
            return null;
        }
        if (-1 == inlineInstances.search(signature) && inlineInstances.size() < INLINE_DEPTH_LIMIT) { // non recursive inlining of the inlined body
            inlineInstances.push(signature);
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
        return result;
    }

    private Node wrappedInlineMethodCall(InlinableCall call) {
        beginSpeculativeCompile();
        Node node = inlineCall(call);
        if (endSpeculativeCompileWithErrors(call)) {
            report("speculative compilation errors", call);
            return null;
        }
        return node;
    }
    
    private Goal.Status savedState;
    private ErrorQueue savedQueue;

    /**
     * Don't let fatal Errors in speculative compilation terminate the containing compile. 
     * Speculative compilation ends with a call to endSpeculativeCompileWithErrors(). 
     * If that method is not called, mayhem will insue!
     */
    private void beginSpeculativeCompile() {
        savedState = job.extensionInfo().scheduler().currentGoal().state();
        // savedQueue = job.compiler().swapErrorQueue(new SimpleErrorQueue());
        savedQueue = job.compiler().swapErrorQueue(new SilentErrorQueue(1024, null));
    }

    /**
     * Terminate a speculative compilation initiated by beginSpeculativeCompile().
     * @param call 
     *
     * @return true is the speculative compilation produced what would have been fatal Errors
     */
    private boolean endSpeculativeCompileWithErrors(InlinableCall call) {
        ErrorQueue speculativeQueue = job.compiler().swapErrorQueue(savedQueue);
        if (0 < speculativeQueue.errorCount()) {
            job.extensionInfo().scheduler().clearFailed();
            job.extensionInfo().scheduler().currentGoal().update(savedState);
            reportSpeculativeErrors(speculativeQueue, call);
            return true;
        }
        return false;
    }

    /**
     * @param speculativeQueue
     * @param call 
     */
    private void reportSpeculativeErrors(ErrorQueue speculativeQueue, InlinableCall call) {
        if (1 <= VERBOSITY && speculativeQueue instanceof SilentErrorQueue) {
            Position pos = call.position();
            List<ErrorInfo> errors = ((SilentErrorQueue) speculativeQueue).getErrors();
            String msg = errors.size()+ " speculative compilation error(s) prevent inlining of " + call;
            for (ErrorInfo error : errors) {
                msg += "\n" +pos+ "\t " +error.getPosition()+ ": " +error;
            }
            Warnings.issue(job, msg, pos);
        }
        speculativeQueue.flush();
    }

    /**
     * @param decl
     * @return
     */
    private String makeSignatureString(ProcedureDecl decl) {
        String id = "";
        id += decl.memberDef().container().get().fullName();
        id += ".";
        id += decl.name();
        id += "(";
        String c = "";
        for (Formal f : decl.formals()) {
            id += c + f.type().nameString();
            c = ",";
        }
        id += ")";
        if (decl instanceof MethodDecl) id += ":" +((MethodDecl) decl).returnType().nameString();
        return id;
    }

    /**
     * Get the declaration corresponding to a call to be inlined.
     * 
     * @param call
     * @return the declaration of the method invoked by call, or null if the
     *         declaration cannot be found, or the call should not be inlined
     */
    private ProcedureDecl getInlineDecl(InlinableCall call) {
        if (DEBUG) debug("Should " + call + " be inlined?", call);
        Position pos = call.position(); // DEBUG
        if (annotationsPreventInlining(call)) {
            report("of annotation at call site", call);
            return null;
        }
        // get inline candidate
        MemberDef candidate = getDef(call);
        // require inlining if either the call of the candidate are so annotated
        inliningRequired = annotationsRequireInlining(call, (X10MemberDef) candidate);
        if (call instanceof ConstructorCall) {
            if (!INLINE_GENERIC_CONSTRUCTORS && !((ConstructorCall) call).typeArguments().isEmpty()) {
                report("inlining not implemented for constructors with type args: " + ((ConstructorCall) call).typeArguments(), call);
                return null;
            }
            if (ts.isStructType(candidate.container().get())) {
                if (INLINE_STRUCT_CONSTRUCTORS)
                    inliningRequired = true;
            }
        }
        // short-circuit if inlining is not required and there is no implicit inlining
        if (!inliningRequired && !INLINE_IMPLICIT) {
            report("inlining not required for candidate: " +candidate, call);
            return null;
        }
        // unless required, skip candidates previously found to be uninlinable
        if (!inliningRequired && getCache().uninlineable(candidate)) {
            report("of previous decision for candidate: " +candidate, call);
            return null;
        }
        // unless required, don't inline if the candidate annotations prevent it
        if (!inliningRequired && annotationsPreventInlining((X10MemberDef) candidate)) {
            report("of annotation on candidate: " + candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
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
        if (annotationsPreventInlining((X10ClassDef) container)) {
            report("of Native Class/Rep annotation of container: " +container, call);
            getCache().notInlinable(candidate);
            return null;
        }
        Job candidateJob = container.job();
        if (null == candidateJob) {
            // TODO: reconstruct job from position
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
        if (annotationsPreventInlining(decl) || annotationsPreventInlining(decl.procedureInstance())) {
            report("candidate declaration annotated to prevent inlining: " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (ExpressionFlattener.javaBackend(job) && hasSuper(decl)) {
            report("candidate body contains super (not yet supported by Java backend): " +candidate, call);
            getCache().notInlinable(candidate);
            return null;
        }
        if (!inliningRequired) { // decide whether to inline candidate
            if (INLINE_IMPLICIT) {
                int cost = getCost(decl, candidateJob);
                if (implicitMax < cost) {
                    report("of excessive cost, " + cost, call);
                    getCache().notInlinable(candidate);
                    return null;
                }
            } else {
                report("inlining not explicitly required", call);
                getCache().notInlinable(candidate);
                return null;
            }
        }
        // remember what to inline this candidate with if we ever see it again
        getCache().putDecl(candidate, decl);
        return decl;
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

    /**
     * @param call
     * @return
     */
    private MemberDef getDef(InlinableCall call) {
        if (call instanceof Call) {
            return ((Call) call).methodInstance().def();
        } 
        if (call instanceof ConstructorCall) {
            return ((ConstructorCall) call).constructorInstance().def();
        }
        if (call instanceof ClosureCall) {
            return ((ClosureCall) call).closureInstance().def();
        }
        return null;
    }

    /**
     * @param def
     * @return
     */
    private boolean annotationsPreventInlining(X10Def def) {
        if (!def.annotationsMatching(NoInlineType).isEmpty())
            return true;
        if (!def.annotationsMatching(NativeRepType).isEmpty())
            return true;
        if (!def.annotationsMatching(NativeClassType).isEmpty())
            return true;
        return false;
    }

    /**
     * @param node
     * @return
     */
    private boolean annotationsPreventInlining(Node node) {
        if (!((X10Ext) node.ext()).annotationMatching(NoInlineType).isEmpty())
            return true;
        return false;
    }

    private String backend;

    /**
     * Get the identity String for the specific backend of this compile
     * @return the backend's identity String for this compilation
     */ // TODO refactor so that the backends and I get there identity strings from the same place (move into ExtensionInfo
    private String getBackend() {
        ExtensionInfo extensionInfo = (ExtensionInfo) ts.extensionInfo();
        if (extensionInfo instanceof x10c.ExtensionInfo)
            return "\"java\"";
        if (extensionInfo instanceof x10cpp.ExtensionInfo)
            return "\"c++\""; 
        if (extensionInfo instanceof x10cuda.ExtensionInfo)
            return "\"cuda\"";
        return "";
    }

    /**
     * @param call
     * @param candidate
     * @return
     */
    private boolean annotationsRequireInlining(InlinableCall call, X10MemberDef candidate) {
        if (!INLINE_METHODS) return false;
        if (!((X10Ext) call.ext()).annotationMatching(InlineType).isEmpty())
            return true;
        if (!candidate.annotationsMatching(InlineType).isEmpty())
            return true;
        return false;
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
     * @param decl
     * @param job
     * @return
     */
    private int getCost(ProcedureDecl decl, Job job) {
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
            return getClosureLiteral(((ParExpr) target).expr());
        // TODO Inline Locals (and field instances?) that have literal closure values
        return null;
    }

    private Node propagateConstants(Node n) {
        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) job().extensionInfo();
        x10Info.stats.startTiming("ConstantPropagator.context", "ConstantPropagator.context");
        Node retNode = n.visit(new ConstantPropagator(job, ts, nf).context(context()));
        x10Info.stats.stopTiming();
        return retNode;
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
        if (ts.typeDeepBaseEquals(expr.type(), toType, context()))
            return expr;
        return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, toType), expr, Converter.ConversionType.UNCHECKED).type(toType);
    }

    private LocalDecl createThisArg(InlinableCall c) {
        if (!(c.target() instanceof Expr))
            return null;
        Expr target = (Expr) c.target();
        if (target instanceof Special && ((Special) target).kind() == Special.SUPER) {
            target = rewriteSuperAsThis((Special) target);
        }
        LocalDef def = ts.localDef(c.target().position(), ts.Final(), Types.ref(c.target().type()), Name.makeFresh("target"));
        LocalDecl ths = syn.createLocalDecl(c.target().position(), def, target);
        return ths;
    }

    /*
     * In general, when the body of one method is inlined into the body of
     * another, the keywords "this" and "super" loose their meanings.
     * InlineRewriter deals with the case of "this". It complains, if it
     * encounters "super". Rewriting "super" as "(this as ST)" won't work
     * because we lose the fact that the call is non-virtual.
     * 
     * However, this rewrite can be used to handle the "this parameter" when
     * inlining calls of the form "super.foo()" (because the method instance has
     * already been resolved). (Java does not allow the bare keyword "super" to
     * occur where an expression is required. It does, of course, allow "this"
     * to be so used. It just needs to be coerced to the right type.)
     */
    private Special rewriteSuperAsThis(Special special) {
        assert (special.kind() == Special.SUPER) : "Unexpected special kind: " +special;
        Special result = nf.Special(special.position(), Special.THIS, special.qualifier());
        result = (Special) result.type(special.type());
        return result;
    }

    private LocalDecl createThisFormal(InlinableCall call, LocalDecl init) {
        if (call instanceof Call && ((Call) call).methodInstance().flags().isStatic()) 
            return null;
        ProcedureInstance<? extends ProcedureDef> pi = call instanceof Call ? ((Call) call).procedureInstance() : ((ConstructorCall) call).procedureInstance();
        TypeParamSubst typeMap = makeTypeMap(pi, call.typeArguments());
        Type thisType = typeMap.reinstantiate(((MemberDef) pi.def()).container().get());
        Expr expr = null == init ? null : createCast(init.position(), syn.createLocal(init.position(), init), thisType);
        LocalDecl thisDecl = syn.createLocalDecl(call.position(), Flags.FINAL, Name.makeFresh("this"), expr);
        return thisDecl;
    }

    // TODO: move this to Position
    private static boolean contains(Position outer, Position inner) {
        if (!outer.file().equals(inner.file()))
            return false;
        if (!outer.path().equals(inner.path()))
            return false;
        return (outer.offset() <= inner.offset() && inner.endOffset() <= inner.endOffset());
    }

    private CodeBlock instantiate(final CodeBlock code, InlinableCall call) {
        try {
            if (DEBUG) debug("Instantiate " + code, call);
            TypeParamSubst typeMap = makeTypeMap(call.procedureInstance(), call.typeArguments());
            InliningTypeTransformer transformer = new InliningTypeTransformer(typeMap);
            ContextVisitor visitor = new NodeTransformingVisitor(job, ts, nf, transformer).context(context());
            CodeBlock visitedCode = (CodeBlock) code.visit(visitor);
            return visitedCode;
        } catch (Exception e) {
            String message = "Exception during instantiation of " +code+ " for " +call+ ": " +e;
            Warnings.issue(job(), message, call.position());
            return null;
        }
    }

    /**
     * @param decl
     * @return
     */
    private TypeParamSubst makeTypeMap(ProcedureInstance<? extends ProcedureDef> instance, List<TypeNode> typeNodes) {
        List<Type> typeArgs = new ArrayList<Type>();
        List<ParameterType> typeParms = new ArrayList<ParameterType>();
        if (!(instance instanceof ConstructorInstance)) { // TODO: remove the condition, currently ConstructorInstances can have a mismatch between type parameters and type arguements but they shouldn't have either so we can ignore them
            if (typeNodes.isEmpty()) {
                typeArgs.addAll(instance.typeParameters());
            } else {
                for (TypeNode tn : typeNodes) {
                    typeArgs.add(tn.type());
                }
            }
            typeParms.addAll(instance.def().typeParameters()); 
        }
        X10ClassType container = (X10ClassType) ((MemberInstance<? extends ProcedureDef>) instance).container();
        if (!instance.def().staticContext()) {
            List<Type> cTypeArgs = container.typeArguments();
            if (cTypeArgs != null) {
                typeArgs.addAll(cTypeArgs);
                typeParms.addAll(container.x10Def().typeParameters());
            }
        }
        if (false) { // TODO enable this path
            assert (typeArgs.size() == typeParms.size());
            return new TypeParamSubst(ts, typeArgs, typeParms);
        }
        // NOTE: the rest of this method is a hack to handle a mismatch that should never occur
        if (typeArgs.size() == typeParms.size()) 
            return new TypeParamSubst(ts, typeArgs, typeParms);
        String msg = "type args/parms mismatch in class " +instance.getClass();
        System.err.println("\nDEBUG: " +msg);
        System.err.println("\n\tposition = "  +instance.position());
        System.err.println("\n\tinstance =  " +instance);
        System.err.println("\n\tcontainer = " +container);
        System.err.println("\n\ttypeArgs = "  +typeArgs);
        System.err.println("\n\ttypeParms = " +typeParms);
        System.err.println();
        assert false; // remove if we ever get here (remove this path if we don't)
        throw new InternalCompilerError(instance.position(), msg);
    }

    // TODO: generate a closure call instead of a statement expression // is this still necessary?
    private Expr rewriteInlinedBody(Position pos, Type retType, List<Formal> formals, Block body, LocalDecl thisArg, LocalDecl thisFormal, List<Expr> args) {

        // Create statement expression from body.
        // body is in normal form:
        // 1) last statement in body is a return statement, and
        // 2) this is the only return in the body.
        List<Stmt> bodyStmts = body.statements();
        if (!(bodyStmts.get(bodyStmts.size() - 1) instanceof Return)) {
            return null; // body could not be normalized, must be something we
                         // cannot yet inline
        }
        Return ret = (Return) bodyStmts.get(bodyStmts.size() - 1);
        List<Stmt> statements = new ArrayList<Stmt>();
        for (Stmt stmt : bodyStmts) {
            if (stmt != ret) {
                statements.add(stmt);
            }
        }
        Expr e = ret.expr();
        if (null != e) {
            assert null != retType; // null retType => constructor call body (which shouldn't return an expr)
            e = createCast(e.position(), e, retType);
        }
        StmtExpr result = syn.createStmtExpr(pos, statements, e);

        // create declarations to prepend to result
        List<Stmt> declarations = new ArrayList<Stmt>();
        if (thisArg != null) { // declare temporary for "this" arg, if call isn't static
            declarations.add(thisArg);
        }
        // initialize temporaries to args
        LocalDecl[] temps = new LocalDecl[args.size()];
        for (int i = 0; i < temps.length; i++) {
            Expr a = args.get(i);
            temps[i] = syn.createLocalDecl(a.position(), Flags.FINAL, Name.makeFresh(), a);
            tieLocalDefToItself(temps[i].localDef());
            declarations.add(temps[i]);
        }
        if (thisFormal != null) { // declare local for "this" parameter, if method isn't static
            declarations.add(thisFormal);
        }
        // initialize new locals (transformed formals) to temporaries (args)
        for (int i = 0; i < temps.length; i++) {
            LocalDecl temp = temps[i];
            X10Formal formal = (X10Formal) formals.get(i);
            Expr value = createCast(temp.position(), syn.createLocal(temp.position(), temp), formal.type().type());
            LocalDecl local = syn.createLocalDecl(formal, value);
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
        type = Types.addSelfBinding(type, CTerms.makeLocal((X10LocalDef) o));
        ((Ref<Type>) d.type()).update(type);
    }

    final InlinerCache getCache() {
        return compiler.getInlinerCache();
    }

}
