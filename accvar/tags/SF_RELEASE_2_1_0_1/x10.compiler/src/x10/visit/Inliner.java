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

import java.lang.ref.SoftReference;
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
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.Throw;
import polyglot.ast.TypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
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
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
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
import x10.constraint.XLocal;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.types.matcher.Subst;

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
public class Inliner extends ContextVisitor {

    private static final boolean DEBUG = false;
//  private static final boolean DEBUG = true;

    private static final boolean VERBOSE = false;
//  private static final boolean VERBOSE = true;
//  private static final boolean VERY_VERBOSE = VERBOSE && false;
    private static final boolean VERY_VERBOSE = VERBOSE && true;

    private String reason;            // move these (DEBUG)
    private boolean inliningRequired; // move these (DEBUG)

    /**
     * The size of the largest method to be considered small, if small methods
     * are to be inlined.
     */
//  private static final int SMALL_METHOD_MAX_SIZE = -1;
//  private static final int SMALL_METHOD_MAX_SIZE = 0;
    private static final int SMALL_METHOD_MAX_SIZE = 1;
//  private static final int SMALL_METHOD_MAX_SIZE = 2;
//  private static final int SMALL_METHOD_MAX_SIZE = 3;

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
    private static final int INITIAL_RECURSION_DEPTH = 0;
    private static final int RECURSION_DEPTH_LIMIT = 2;

    private void debug(String msg, Node node) {
        if (!DEBUG)
            return;
        try {
            Thread.sleep(10);
            System.out.print("  DEBUG ");
            if (null != node)
                System.out.print(node.position() + ":  ");
            System.out.println(msg);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore exception (we are just trying to avoid stepping on writes to STDERR
        }
    }

    /**
     * Annotation types.
     */
    private Type InlineType;
    private Type InlineOnlyType;
    private Type NoInlineType;
    private Type NativeType;
    private Type NativeRepType;
    private Type NativeClassType;

    /**
     * Names of the annotation types that pertain to inlining.
     */
    private static final QName INLINE_ANNOTATION       = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION  = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION    = QName.make("x10.compiler.NoInline");
    private static final QName NATIVE_ANNOTATION       = QName.make("x10.compiler.Native");
    private static final QName NATIVE_REP_ANNOTATION   = QName.make("x10.compiler.NativeRep");
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");

    /**
     * 
     */
    private X10TypeSystem xts;
    private X10NodeFactory xnf;
    // private Synthesizer syn;
    private ForLoopOptimizer syn; // move functionality to Synthesizer
    private InlineCostEstimator ice;
    private SoftReference<InlinerCache> inlinerCacheRef[] = (SoftReference<InlinerCache>[]) new SoftReference<?>[1];

    public Inliner(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
        // syn = new Synthesizer(xnf, xts);
        syn = new ForLoopOptimizer(job, ts, nf);
        ice = new InlineCostEstimator(xts, xnf);
    }

    @Override
    public NodeVisitor begin() {
        recursionDepth[0] = INITIAL_RECURSION_DEPTH;
        try {
            InlineType = (Type) ts.systemResolver().find(INLINE_ANNOTATION);
            InlineOnlyType = (Type) ts.systemResolver().find(INLINE_ONLY_ANNOTATION);
            NoInlineType = (Type) ts.systemResolver().find(NO_INLINE_ANNOTATION);
            NativeType = (Type) ts.systemResolver().find(NATIVE_ANNOTATION);
            NativeRepType = (Type) ts.systemResolver().find(NATIVE_REP_ANNOTATION);
            NativeClassType = (Type) ts.systemResolver().find(NATIVE_CLASS_ANNOTATION);
        } catch (SemanticException e) {
            InternalCompilerError ice = new InternalCompilerError("Unable to find required Annotation Type (Inlining disabled!)");
            SemanticException se = new SemanticException(ice);
            Errors.issue(job, se);
            InlineType = null;
        }
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
        if (node instanceof X10MethodDecl) {
            return null; // @NoInline annotation means something else
        }
        if (node instanceof Call) {
            return null; // will handle @NoInline annotation seperately
        }
        if (node instanceof ClassDecl) {
            if ( !((X10Ext) node.ext()).annotationMatching(NativeClassType) .isEmpty() || 
                 !((X10Ext) node.ext()).annotationMatching(NativeRepType).isEmpty() ) {
                // debug("Native Class/Rep: short-circuiting inlining for children of " +node, node);
                return node;
            }
        }
        if (ExpressionFlattener.cannotFlatten(node)) {
            debug("Cannot flatten: short-circuiting inlining for children of " + node, node);
            return node; // don't inline inside Nodes that cannot be Flattened
        }
        if (node.ext() instanceof X10Ext) {
            if (!((X10Ext) node.ext()).annotationMatching(NoInlineType).isEmpty()) { // short-circuit inlining decisions
                debug("Explicit annotation: short-circuiting inlining for children of " + node, node);
                return node;
            }
        }
        return null;
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Node result = n;
        if (n instanceof X10Call) {
            result = inlineMethodCall((X10Call_c) n);
        } else if (n instanceof ClosureCall) {
            result = inlineClosureCall((ClosureCall) n);
        } else if (n instanceof X10MethodDecl) {
            return nonInlineOnlyMethods((X10MethodDecl) n); // ASK: is this the right way to remove a method decl from the ast
        } else {
            return result;
        }
        if (null == result) { // don't inline this call
            result = n;
        }
        if (n != result) {
            if (VERBOSE) {
                Warnings.issue(job, "___ Inlining: " + n, n.position());
                if (DEBUG && VERY_VERBOSE && false) {
                    System.err.println("\n\nat\t" + n.position() + "\ninlining:\t" + n);
                    result.dump(System.err);
                }
            }
        } else {
            if (VERY_VERBOSE || inliningRequired) {
                Warnings.issue(job, "NOT Inlining: " + n + " (because " + reason + ")", n.position());
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
        if (null == InlineType)
            return call;
        beginSpeculativeCompile();
        Expr result = call;
        X10MethodDecl decl = getInlineDecl(call);
        if (null != decl) {
            String methodId = getMethodId(decl);
            decl = instantiate(decl, call);
            // TODO: handle "this" parameter like formals (cache actual in temp, assign to local (transformed this)
            LocalDecl thisArg = createThisArg(call);
            LocalDecl thisForm = createThisFormal((X10MethodInstance) call.methodInstance(), thisArg);
            decl = normalizeMethod(decl, thisForm); // Ensure that the last statement of the body is the only return in the method
            result = rewriteInlinedBody( call.position(), 
                                         decl.returnType().type(), 
                                         decl.formals(), 
                                         decl.body(), 
                                         thisArg, 
                                         thisForm, 
                                         call.arguments() );
            if (null == result) {
                reason = "body doesn't contain a return (throw only)";
                debug(reason, call);
                return null;
            }
            result = (Expr) result.visit(new X10AlphaRenamer());
            if (-1 == inlineInstances.search(methodId)) { // non recursive inlining of the inlined body
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
            return call; // DO NOT INLINE! // this may be overly conservative (at least in some cases)
        }
        return result;
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
     *
     * @return true is the speculative compilation produced what would have been fatal Errors
     */
    private boolean endSpeculativeCompileWithErrors() {
        ErrorQueue speculativeQueue = job.compiler().swapErrorQueue(savedQueue);
        if (0 < speculativeQueue.errorCount()) {
            job.extensionInfo().scheduler().clearFailed();
            job.extensionInfo().scheduler().currentGoal().update(savedState);
            reportSpeculativeErrors(speculativeQueue);
            return true;
        }
        return false;
    }

    /**
     * @param speculativeQueue
     */
    private void reportSpeculativeErrors(ErrorQueue speculativeQueue) {
        if (!VERBOSE)
            return;
        try {
            if (speculativeQueue instanceof SilentErrorQueue) {
                System.err.flush();
                java.lang.Thread.sleep(1);
                System.out.println("The following errors were ignored during speculative compilation:");
                for (Object o : ((SilentErrorQueue) speculativeQueue).getErrors()) {
                    System.out.print("   >>> ");
                    if (o instanceof ErrorInfo)
                        System.out.print(((ErrorInfo) o).getPosition());
                    System.out.println(": " + o);
                }
                System.out.flush();
                java.lang.Thread.sleep(1);
            } else {
                speculativeQueue.flush();
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * @param decl
     * @return
     */
    private String getMethodId(X10MethodDecl decl) {
        String methodId = getContainer((X10MethodDef) decl.methodDef()).fullName() + "." + decl.name() + "(";
        String c = "";
        for (Formal f : decl.formals()) {
            methodId += c + f.type().nameString();
            c = ",";
        }
        methodId += "):" + decl.returnType().nameString();
        return methodId;
    }

    private Expr inlineClosureCall(ClosureCall c) {
        Closure lit = getInlineClosure(c);
        if (null == lit)
            return c;
        List<Expr> args = new ArrayList<Expr>();
        int i = 0;
        for (Expr a : c.arguments()) {
            args.add(createCast(a.position(), a, c.closureInstance().formalTypes().get(i++)));
        }
        lit = normalizeClosure(lit); // Ensure that the last statement of the body is the only return in the closure
        Expr result = rewriteInlinedBody(c.position(), lit.returnType().type(), lit.formals(), lit.body(), null, null, args);
        result = (Expr) result.visit(new X10AlphaRenamer());
        result = (Expr) result.visit(this);
        result = (Expr) propagateConstants(result);
        return result;
    }

    private class X10AlphaRenamer extends AlphaRenamer {
        public class TypeRewriter extends TypeTransformer {

            @Override
            protected Type transformType(Type type) {
                try {
                    Set<Name> vars = renamingMap.keySet();
                    XVar[] x = new XVar[vars.size() + 1];
                    XTerm[] y = new XTerm[x.length];
                    int i = 0;
                    for (Name n : vars) {
                        Name m = renamingMap.get(n);
                        LocalDef ld = localDefMap.get(n);
                        x[i] = XTerms.makeLocal(XTerms.makeName(ld, n.toString()));
                        y[i] = XTerms.makeLocal(XTerms.makeName(ld, m.toString()));
                        ++i;
                    }
                    x[i] = XTerms.makeFreshLocal(); // to force substitution
                    y[i] = XTerms.makeFreshLocal();
                    type = Subst.subst(type, y, x);
                } catch (SemanticException e) {
                    throw new InternalCompilerError("Cannot alpha-rename locals in type " + type, e);
                }
                return super.transformType(type);
            }

            @Override
            protected ParameterType transformParameterType(ParameterType pt) {
                // TODO: [IP] Do we need to do anything with parameter types?
                return super.transformParameterType(pt);
            }

            @Override
            protected X10LocalInstance transformLocalInstance(X10LocalInstance li) {
                Name name = renamingMap.get(li.name());
                if (name != null) {
                    // List<Type> annotations = transformTypeList(li.annotations()); // TODO
                    Type type = transformType(li.type());
                    if (/* li.annotations() != annotations || */li.name() != name || li.type() != type) {
                        li = li/* .annotations(annotations) */.name(name).type(type);
                    }
                }
                return super.transformLocalInstance(li);
            }

            @Override
            protected X10FieldInstance transformFieldInstance(X10FieldInstance fi) {
                // TODO: [IP] We don't change field instances yet, but would have to for local classes
                return super.transformFieldInstance(fi);
            }

            @Override
            protected X10MethodInstance transformMethodInstance(X10MethodInstance mi) {
                // TODO: [IP] We don't change method instances yet, but would have to for local classes
                return super.transformMethodInstance(mi);
            }

            @Override
            protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
                // TODO: [IP] We don't change constructor instances yet, but would have to for local classes
                return super.transformConstructorInstance(ci);
            }
        }

        protected TypeRewriter rewriter = new TypeRewriter();
        protected Map<Name, LocalDef> localDefMap = new HashMap<Name, LocalDef>();

        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof LocalDecl) {
                LocalDecl l = (LocalDecl) n;
                localDefMap.put(l.name().id(), l.localDef());
            }
            return super.enter(n);
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            Set<Name> s = null;
            if (n instanceof Block) {
                s = setStack.peek();
            }
            Node res = super.leave(old, n, v);
            res = rewriter.transform(res, old, Inliner.this);
            if (res instanceof Block) {
                localDefMap.keySet().removeAll(s);
            }
            return res;
        }
    }

    /**
     * Get the declaration corresponding to a call to be inlined.
     * 
     * @param call
     * @return the declaration of the method invoked by call, or null if the
     *         declaration cannot be found, or the call should not be inlined
     */
    private X10MethodDecl getInlineDecl(X10Call_c call) {
        // System.out.println("                considering inlining "+ call);
        debug("Inline " + call + " ?", call);

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
        if (!inliningRequired && !getInlinerCache().inlinable(candidate)) {
            reason = "of previous decision for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            return null;
        }

        // unless required, don't inline if the candidate annotations prevent it
        if (!inliningRequired && annotationsPreventInlining(candidate)) {
            reason = "of annotation on candidate: " + candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            return null;
        }

        // see if the declaration for this candidate has already been cached
        X10MethodDecl decl = getInlinerCache().getDecl(candidate);
        if (null != decl) {
            return decl;
        }

        // get container and declaration for inline candidate
        X10ClassDef container = getContainer(candidate);
        if (null == container) {
            reason = "unable to find container for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            return null;
        }
        if (isVirtualOrNative(candidate, container)) {
            reason = "call is virtual or native on candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            return null;
        }
        Job candidateJob = getJob(candidate, container);
        if (null == candidateJob) {
            reason = "unable to find job for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            getInlinerCache().badJob(candidateJob);
            return null;
        }
        if (annotationsPreventInlining(container)) {
            reason = "of Native Class/Rep annotation of container: " +container;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            getInlinerCache().badJob(candidateJob);
        }

        Node ast = candidateJob.ast();
        if (null == ast) {
            reason = "unable to find ast for candidated: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            getInlinerCache().badJob(candidateJob);
            return null;
        }

        decl = getDeclaration(candidate, ast);
        if (null == decl) {
            reason = "unable to find declaration for candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            return null;
        }

        if (this.job.compiler().errorQueue().hasErrors()) { // This may be overly conservative
            reason = "there were errors compiling candidate: " +candidate;
            debug("Inlining failed because " + reason, call);
            getInlinerCache().notInlinable(candidate);
            getInlinerCache().badJob(candidateJob);
            return null;
        }

        if (!inliningRequired) { // decide whether to inline candidate
            if (x10.Configuration.INLINE_SMALL_METHODS) {
                int cost = getCost(decl, candidateJob);
                if (SMALL_METHOD_MAX_SIZE < cost) {
                    reason = "of excessive cost, " + cost;
                    debug("Inlining failed because " + reason, call);
                    getInlinerCache().notInlinable(candidate);
                    return null;
                } else {
                    // debug("Inlining small method with cost " + cost, call);
                }
            } else {
                reason = "inlining not explicitly required";
                debug("Inlining failed because " + reason, call);
                getInlinerCache().notInlinable(candidate);
                return null;
            }
        }
        // remember what to inline this candidate with if we ever see it again
        getInlinerCache().putDecl(candidate, decl);
        return decl;
    }

    /**
     * @param container
     * @return
     */
    private boolean annotationsPreventInlining(X10ClassDef container) {
        if (!container.annotationsMatching(NativeRepType).isEmpty())
            return true;
        if (!container.annotationsMatching(NativeClassType).isEmpty())
            return true;
        return false;
    }

    /**
     * @param call
     * @return
     */
    private boolean annotationsPreventInlining(X10Call_c call) {
        if (!((X10Ext) call.ext()).annotationMatching(NoInlineType).isEmpty())
            return true;
        return false;
    }

    /**
     * @param candidate
     * @return
     */
    private boolean annotationsPreventInlining(X10MethodDef candidate) {
        if (!candidate.annotationsMatching(NoInlineType).isEmpty())
            return true;
        return hasRelevantNativeAnnotation(candidate
                .annotationsMatching(NativeType));
    }

    private String backend;

    /**
     * @param nativeAnnotations
     * @return
     */
    private boolean hasRelevantNativeAnnotation(
            List<? extends Type> nativeAnnotations) {
        if (!nativeAnnotations.isEmpty()) {
            if (null == backend)
                backend = getBackend();
            for (Type t : nativeAnnotations) {
                String lang = ((X10ParsedClassType) t).propertyInitializer(0).toString();
                if (backend.equals(lang))
                    return true;
            }
        }
        return false;
    }

    /**
     * Get the identity String for the specific backend of this compile
     * @return the backend's identity String for this compilation
     */ // TODO refactor so that the backends and I get there identity strings from the same place (move into ExtensionInfo
    private String getBackend() {
        ExtensionInfo extensionInfo = xts.extensionInfo();
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
    private boolean annotationsRequireInlining(X10Call_c call,
            X10MethodDef candidate) {
        if (!((X10Ext) call.ext()).annotationMatching(InlineType).isEmpty())
            return true;
        if (!candidate.annotationsMatching(InlineType).isEmpty())
            return true;
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
     * @return true, if the method obviously should not be inlined; false, otherwise
     */
    private boolean isVirtualOrNative(X10MethodDef candidate, X10ClassDef container) {
        Flags mf = candidate.flags();
        Flags cf = container.flags();
        if (!mf.isFinal() && !mf.isPrivate() && !mf.isStatic() && !cf.isFinal() && !container.isStruct())
            return true;
        if (mf.isNative() || cf.isNative())
            return true;
        return false;
    }

    /**
     * Obtain the job for containing the declaration for a given method. Run the
     * preliminary compilation phases on the job's AST.
     * 
     * Note Errors during speculative compilation should not be fatal. The
     * mechanism implementing this behavior consists of a pair of hacks that
     * should be fixed.
     * 
     * @param candidate
     * @param container
     * @return
     */
    private Job getJob(X10MethodDef candidate, X10ClassDef container) {
        Position pos = candidate.position();
        Job job = container.job();
        try {
            /*
             * TODO: reconstruct job form position 
             * if (null == job) { 
             * String file = pos.file(); 
             * String path = pos.path(); 
             * Source source = new Source(file, path, null); job = xts.extensionInfo().scheduler().addJob(source); }
             */
            if (null == job) {
                debug("Unable to find or create job for method: " + candidate, null);
                return null;
            } else if (!getInlinerCache().okayJob(job)) {
                return null;
            } else if (job != this.job()) {
                debug("Looking for job: " + job, null);
                String key = container.fullName().toString().intern();
                // String key = job.toString();
                Node ast = getInlinerCache().getAST(key);
                if (null == ast) {
                    if (null == job.ast()) {
                        getInlinerCache().badJob(job);
                        return null;
                    }
                    // TODO reconstruct the AST for the job will all preliminary compiler passes
                    ast = job.ast().visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin());
                    if (null == ast) {
                        debug("Unable to reconstruct AST for " + job, null);
                        getInlinerCache().badJob(job);
                        return null;
                    }
                    debug("Reconstructed AST for " + job, null);
                    job.ast(ast); // ASK: why does this work?
                    getInlinerCache().putAST(key, ast);
                }
                // job.ast(ast); // ASK: why doesn't this work?
            }
        } catch (Exception x) {
            getInlinerCache().badJob(job);
            String msg = "AST for job, " + job + " (for candidate " + candidate + ") does not typecheck (" + x + ")";
            debug(msg, null);
            SemanticException e = new SemanticException(msg, candidate.position());
            Errors.issue(job, e);
            return null;
        }
        return job;
    }

    /**
     * Walk an AST looking for the declaration of a given method.
     * 
     * @param candidate the method whose declaration is desired
     * @param ast the abstract syntax tree containing the declaration
     * @return the declaration for the indicated method, or 
     *         null if no declaration can be found or it has an empty body.
     */
    private X10MethodDecl getDeclaration(final X10MethodDef candidate,
            final Node ast) {
        final Position pos = candidate.position();
        final X10MethodDecl[] decl = new X10MethodDecl[1];
        ast.visit(new NodeVisitor() { // find the declaration of md
                    public Node override(Node n) {
                        if (null != decl[0])
                            return n; // we've already found the decl, short-circuit search
                        if (n instanceof TypeNode)
                            return n; // TypeNodes don't contain decls, short-circuit search
                        if (!pos.isCompilerGenerated()&& !contains(n.position(), pos))
                            return n; // definition of md isn't inside n, short-circuit search
                        if (n instanceof X10MethodDecl && candidate == ((X10MethodDecl) n).methodDef()) {
                            decl[0] = (X10MethodDecl) n;
                            return n; // we found the decl for the candidate, short-circuit search
                        }
                        return null; // look for the decl inside n
                    }
                });
        if (null == decl[0]) {
            reason = "Declaration not found for " +candidate;
            debug(reason, null);
            if (VERBOSE) Warnings.issue(job, reason, pos);
            return null;
        }
        if (null == decl[0].body()) {
            reason = "No declaration body for " +decl[0]+ " (" +candidate+ ")";
            debug(reason, null);
            if (VERBOSE) Warnings.issue(job, reason, pos);
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
            return getClosureLiteral(((ParExpr) target).expr());
        // TODO Inline Locals (and field instances?) that have literal closure values
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
        return n.visit(new ConstantPropagator(job, ts, nf).context(context()));
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
        if (xts.typeDeepBaseEquals(expr.type(), toType, context()))
            return expr;
        return xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, toType), expr,
                Converter.ConversionType.UNCHECKED).type(toType);
    }

    private LocalDecl createThisArg(X10Call c) {
        if (!(c.target() instanceof Expr))
            return null;
        Expr target = (Expr) c.target();
        if (target instanceof Special && ((Special) target).kind() == Special.SUPER) {
            target = rewriteSuperAsThis((Special) target);
        }
        LocalDef def = xts.localDef(c.target().position(), xts.Final(), Types.ref(c.target().type()), Name.make("target"));
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
     * to be so used. It just needs to be coersed to the right type.)
     */
    private Special rewriteSuperAsThis(Special special) {
        assert (special.kind() == Special.SUPER) : "Unexpected special kind: " +special;
        Special result = xnf.Special(special.position(), Special.THIS, special.qualifier());
        result = (Special) result.type(special.type());
        return result;
    }

    private LocalDecl createThisFormal(X10MethodInstance mi, LocalDecl init) {
        if (mi.flags().isStatic())
            return null;
        TypeParamSubst typeMap = makeTypeMap(mi);
        Type thisType = typeMap.reinstantiate(mi.def().container().get());
        Expr expr = null == init ? null : createCast(init.position(), syn.createLocal(init.position(), init), thisType);
        LocalDecl thisDecl = syn.createLocalDecl(mi.position(), Flags.FINAL, Name.make("this"), expr);
        return thisDecl;
    }

    private LocalDef computeThis(MethodDef def) {
        // X10TypeSystem xts = (X10TypeSystem) typeSystem();
        if (def.flags().isStatic())
            return null;
        return xts.localDef(def.position(), xts.Final(), def.container(), Name.makeFresh("this"));
    }

    // TODO: move this to Position
    private static boolean contains(Position outer, Position inner) {
        if (!outer.file().equals(inner.file()))
            return false;
        if (!outer.path().equals(inner.path()))
            return false;
        return (outer.offset() <= inner.offset() && inner.endOffset() <= inner.endOffset());
    }

    private X10MethodDecl instantiate(final X10MethodDecl decl, X10Call c) {
        debug("Instantiate " + decl, c);
        InliningTypeTransformer transformer = new InliningTypeTransformer(makeTypeMap(c.methodInstance()));
        ContextVisitor visitor = new NodeTransformingVisitor(job, ts, nf, transformer).context(context());
        X10MethodDecl visitedDecl = (X10MethodDecl) decl.visit(visitor);
        return visitedDecl;
    }

    public static class InliningTypeTransformer extends TypeParamSubstTransformer {
        protected InliningTypeTransformer(TypeParamSubst subst) {
            super(subst);
        }

        // TODO: move this up to TypeTransformer
        private Pair<XLocal[], XLocal[]> getLocalSubstitution() {
            HashMap<X10LocalDef, X10LocalDef> map = vars;
            XLocal[] X = new XLocal[map.keySet().size()];
            XLocal[] Y = new XLocal[X.length];
            int i = 0;
            for (X10LocalDef ld : map.keySet()) {
                X[i] = XTerms.makeLocal(new XNameWrapper<X10LocalDef>(ld));
                Y[i] = XTerms.makeLocal(new XNameWrapper<X10LocalDef>(map.get(ld)));
                i++;
            }
            return new Pair<XLocal[], XLocal[]>(X, Y);
        }

        @Override
        protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                ci = Subst.subst(ci, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+ci, e);
            }
            return super.transformConstructorInstance(ci);
        }

        @Override
        protected X10FieldInstance transformFieldInstance(X10FieldInstance fi) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                fi = Subst.subst(fi, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+fi, e);
            }
            return super.transformFieldInstance(fi);
        }

        @Override
        protected X10LocalInstance transformLocalInstance(X10LocalInstance li) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                li = Subst.subst(li, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+li, e);
            }
            return super.transformLocalInstance(li);
        }

        @Override
        protected X10MethodInstance transformMethodInstance(X10MethodInstance mi) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                mi = Subst.subst(mi, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+mi, e);
            }
            return super.transformMethodInstance(mi);
        }

        @Override
        protected Type transformType(Type type) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                type = Subst.subst(type, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+type, e);
            }
            return super.transformType(type);
        }

        @Override
        protected <T> Ref<T> transformRef(Ref<T> ref) {
            return remapRef(ref);
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
                assert (false) : "Not yet implemented, can't instantiate " + s;
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
            assert (false) : "Not yet implemented, can't instantiate " + d;
            return d;
        }

        @Override
        protected X10ConstructorDecl transform(X10ConstructorDecl d, X10ConstructorDecl old) {
            assert (false) : "Not yet implemented, can't instantiate " + d;
            return d;
        }

        @Override
        protected X10MethodDecl transform(X10MethodDecl d, X10MethodDecl old) {
            boolean sigChanged = d.returnType() != old.returnType();
            List<Formal> params = d.formals();
            List<Formal> oldParams = old.formals();
            for (int i = 0; i < params.size(); i++) {
                sigChanged |= params.get(i) != oldParams.get(i);
            }
            sigChanged |= d.guard() != old.guard();
            List<Ref<? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
            SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(visitor().typeSystem()) : d.exceptions();
            SubtypeSet oldExcs = old.exceptions();
            if (null != excs) {
                for (Type et : excs) {
                    sigChanged |= !oldExcs.contains(et);
                    excTypes.add(Types.ref(et));
                }
            }
            sigChanged |= d.offerType() != old.offerType();
            if (sigChanged) {
                List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
                List<LocalDef> formalNames = new ArrayList<LocalDef>();
                for (int i = 0; i < params.size(); i++) {
                    Formal p = params.get(i);
                    argTypes.add(p.type().typeRef());
                    formalNames.add(p.localDef());
                }
                return d.methodDef(createMethodDef(d, argTypes, formalNames));
            }
            return d;
        }

        /**
         * @param d
         * @param argTypes
         * @param formalNames
         * @return
         */
        private X10MethodDef createMethodDef(X10MethodDecl d, List<Ref<? extends Type>> argTypes, List<LocalDef> formalNames) {
            X10MethodDef md = d.methodDef();
            DepParameterExpr g = d.guard();
            TypeNode ot = d.offerType();
            return ((X10TypeSystem) visitor().typeSystem()).methodDef( md.position(), 
                                                                       md.container(), 
                                                                       md.flags(), 
                                                                       d.returnType().typeRef(), 
                                                                       md.name(), 
                                                                       md.typeParameters(), 
                                                                       argTypes, 
                                                                       md.thisVar(), 
                                                                       formalNames, 
                                                                       g == null ? null : g.valueConstraint(),
                                                                       g == null ? null : g.typeConstraint(),
                                                                       ot == null ? null : ot.typeRef(), 
                                                                       null /* the body will never be used */ );
        }
    }

    /**
     * @param decl
     * @return
     */
    private TypeParamSubst makeTypeMap(X10MethodInstance method) {
        List<Type> typeArgs = new ArrayList<Type>();
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
            LocalDecl local = syn.transformFormalToLocalDecl(formal, value);
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
            type = X10TypeMixin.addSelfBinding(type, XTerms.makeLocal(XTerms.makeName(o, o.name().toString())));
        } catch (XFailure e) {
        }
        ((Ref<Type>) d.type()).update(type);
    }

    /**
     * Rewrites a given method/closure body so that it has exactly one return
     * statement at the end if the return type is not void, and no return
     * statements if it's void. Also, replaces "this" parameter by a local
     * variable.
     * 
     * @author igor TODO: factor out into its own class
     */
    public class InliningRewriter extends ContextVisitor {
        private final FunctionDef def;
        private final LocalDef ths;
        private final LocalDef ret;
        private final Name label;
        private int returnCount;
        private int throwCount;

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
            this.returnCount = 0;
            this.throwCount = 0;
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
            if (def == null)
                return n;
            return null;
        }

        // TODO: use override to short-circuit the traversal
        public Node leaveCall(Node old, Node n, NodeVisitor v)
                throws SemanticException {
            if (v != this) {
                this.returnCount += ((InliningRewriter) v).returnCount;
                this.throwCount += ((InliningRewriter) v).throwCount;
            }
            if (n instanceof AmbExpr || n instanceof AmbAssign || n instanceof AmbTypeNode) {
                throw new InternalCompilerError("Ambiguous node found: " + n, n.position());
            }
            if (n instanceof X10MethodDecl)
                return visitMethodDecl((X10MethodDecl) n);
            if (n instanceof Closure)
                return visitClosure((Closure) n);
            if (n instanceof Return)
                return visitReturn((Return) n);
            if (n instanceof Throw)
                return visitThrow((Throw) n);
            if (n instanceof Field)
                return visitField((Field) n);
            if (n instanceof Call)
                return visitCall((X10Call) n);
            if (n instanceof Special)
                return visitSpecial((Special) n);
            return n;
        }

        private Block rewriteBody(Position pos, Block body) throws SemanticException {
            // returnCount = 1; // turn of returnCount algorithm DEBUG
            if (label == null) {
                return body;
            }
            if (returnCount == 0 && throwCount != 0) {
                // A body with no non-exceptional exit
                return body;
            }
            X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
            X10TypeSystem xts = (X10TypeSystem) typeSystem();
            List<Stmt> newBody = new ArrayList<Stmt>();
            if (ret != null) {
                newBody.add(xnf.LocalDecl( pos,
                                           xnf.FlagsNode(pos, xts.NoFlags()),
                                           xnf.CanonicalTypeNode(pos, ret.type()),
                                           xnf.Id(pos, ret.name()) ).localDef(ret));
            }
            if (0 < returnCount) {
                newBody.add(xnf.Labeled(pos, xnf.Id(pos, label), xnf.Do(pos, body, syn.createFalse(pos))));
            } else {
                newBody.addAll(body.statements());
            }
            if (ret != null) {
                Expr rval = xnf.Local(pos, xnf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(ret.type().get());
                newBody.add(xnf.Return(pos, rval));
            } else {
                newBody.add(xnf.Return(pos));
            }
            return xnf.Block(body.position(), newBody);
        }

        // def m(`x:`T):R=S -> def m(`x:`T)={r:R; L:do{ S[return v/r=v; break
        // L;]; }while(false); return r;}
        private X10MethodDecl visitMethodDecl(X10MethodDecl n) throws SemanticException {
            // First check that we are within the right method
            if (n.methodDef() != def)
                return n;
            return (X10MethodDecl) n.body(rewriteBody(n.position(), n.body()));
        }

        // (`x:`T):R=>S -> (`x:`T)=>{r:R; L:do{ S[return v/r=v; break L;];
        // }while(false); return r;}
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
            this.returnCount++;
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

        // throw e; -> throw e;
        private Stmt visitThrow(Throw n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (label == null) return n;
            this.throwCount++;
            return n;
        }

        private Expr getThis(Position pos) {
            X10NodeFactory xnf = (X10NodeFactory) nf;
            if (null == ths) {
                // System.err.println("Missing ths at " +pos);
            }
            LocalInstance tli = ths.asInstance();
            return xnf.Local(pos, xnf.Id(pos, tli.name())).localInstance(tli).type(tli.type());
        }

        // f -> ths.f
        private Field visitField(Field n) {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (!n.isTargetImplicit()) return n;
            FieldInstance fi = n.fieldInstance();
            if ((ths == null) != (fi.flags().isStatic())) {
                // System.err.println("Bad field: " +n+ " at " +n.position());
            }
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
            if ((ths == null) != (mi.flags().isStatic())) {
                System.out.println("DEBUG: visitCall assert failure at " + n.position() + " call=" + n + " method=" + mi);
                System.out.println();
            }
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
            if (null != n.qualifier()) {
                throw new InternalCompilerError("qualified this not supported when inlining", n.position());
            }
            Position pos = n.position();
            return getThis(pos);
        }
    }

    private class InlineCostEstimator extends X10DelegatingVisitor {
        private static final int NATIVE_CODE_COST = 989898;
        int cost;
        X10TypeSystem xts;
        X10NodeFactory xnf;

        InlineCostEstimator(X10TypeSystem ts, X10NodeFactory nf) {
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

        public final void visit(ProcedureCall c) {
            cost++;
            visit((Node) c);
        }

        public final void visit(ClassMember c) { // never inline these ??
            cost += 100;
            visit((Node) c);
        }

        public final void visit(Special c) {
            cost++;
            visit((Node) c);
        }

        public final void visit(Node n) {
            if (n.ext() instanceof X10Ext) {
                if (hasRelevantNativeAnnotation(((X10Ext) n.ext()).annotationMatching(NativeType))) {
                    cost = NATIVE_CODE_COST;
                }
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

        /*
         * (non-Javadoc)
         * 
         * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node,
         * polyglot.ast.Node, polyglot.visit.NodeVisitor)
         */
        @Override
        protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            ice.visitAppropriate(n);
            return n;
        }
    }

    private class InlinerCache {
        private final Set<X10MethodDef> dontInline = new HashSet<X10MethodDef>();
        private final Map<X10MethodDef, X10MethodDecl> def2decl = new HashMap<X10MethodDef, X10MethodDecl>();
        private final Set<Job> badJobs = new HashSet<Job>();
        private final Map<String, Node> astMap = new HashMap<String, Node>();

        boolean inlinable(X10MethodDef candidate) {
            return !dontInline.contains(candidate);
        }

        void notInlinable(X10MethodDef candidate) {
            dontInline.add(candidate);
        }

        boolean okayJob(Job job) {
            return !badJobs.contains(job);
        }

        void badJob(Job job) {
            badJobs.add(job);
        }

        X10MethodDecl getDecl(X10MethodDef candidate) {
            return def2decl.get(candidate);
        }

        void putDecl(X10MethodDef candidate, X10MethodDecl decl) {
            def2decl.put(candidate, decl);
        }

        Node getAST(String name) {
            return astMap.get(name);
        }

        void putAST(String name, Node ast) {
            astMap.put(name, ast);
        }

    }

//    static Object inlinerCache = null;
    final InlinerCache getInlinerCache() {
//        if (null == inlinerCache)inlinerCache = new InlinerCache();
//        if (inlinerCache instanceof InlinerCache) return (InlinerCache) inlinerCache;
        if (null == inlinerCacheRef[0] || null == inlinerCacheRef[0].get()) {
            inlinerCacheRef[0] = new SoftReference<InlinerCache>(new InlinerCache());
        }
        return inlinerCacheRef[0].get();
    }

}
