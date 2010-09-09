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
import java.util.Iterator;
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
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.types.ClassType;
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
import polyglot.util.SimpleErrorQueue;
import polyglot.util.SubtypeSet;
import polyglot.visit.AlphaRenamer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.AssignPropertyBody;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10ConstructorCall;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.ast.X10New;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.constraint.XFailure;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.AnnotatedType;
import x10.types.ClosureInstance;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
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

    private static final boolean DEBUG = false;
 // private static final boolean DEBUG = true;

    /**
     * This constant controls accesses to inaccessible fields.
     */
    private static final boolean ALLOW_STMTEXPR = true;

    /**
     * Names of the annotation classes that govern inlining.
     */
    private static final QName INLINE_ANNOTATION      = QName.make("x10.compiler.Inline");
    private static final QName INLINE_ONLY_ANNOTATION = QName.make("x10.compiler.InlineOnly");
    private static final QName NO_INLINE_ANNOTATION   = QName.make("x10.compiler.NoInline");

    /**
     * The cached type of the @Inline and @NoInline annotations.
     */
    private Type InlineType;
    private Type InlineOnlyType;
    private Type NoInlineType;

    /**
     * The size of the largest method to be considered small, if small methods are to be inlined.
     */
    private static final int  SMALL_METHOD_MAX_SIZE = 1;

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
     * Cache the decision to inline a method and the AST to use for it.
     */
    private static final Set<X10MethodDef> dontInline              = new HashSet<X10MethodDef>();
    private static final Map<X10MethodDef, X10MethodDecl> def2decl = new HashMap<X10MethodDef, X10MethodDecl>();

    private TypeParamSubst typeMap;

    /**
     * 
     */
    X10TypeSystem xts;
    X10NodeFactory xnf;
//    Synthesizer syn;
    ForLoopOptimizer syn; // move functionality to Synthesizer
    InlineCostEstimator ice;

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
        return super.begin();
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!ALLOW_STMTEXPR) return n;  // FIXME: for now
        if (n instanceof X10Call) return inlineMethodCall((X10Call_c) n);
        if (n instanceof ClosureCall) return inlineClosureCall((ClosureCall) n);
        if (n instanceof X10MethodDecl) 
            return nonInlineOnlyMethods((X10MethodDecl) n);
        return n;
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

    private Expr inlineMethodCall(X10Call_c c) {
        if (null == InlineType) return c;
        try {
            X10MethodDecl decl = getInlineDecl(c);
            if (null == decl) return c;
            String methodId = getMethodId(decl);
            decl = instantiate(decl, c);
            // TODO: handle "this" parameter like formals (cache actual in temp, assign to local (transformed this)
            LocalDecl thisArg  = createThisArg(c);
            LocalDecl thisForm = createThisFormal((X10MethodInstance) c.methodInstance(), thisArg);
            decl = normalizeMethod(decl, thisForm); // Ensure that the last statement of the body is the only return in the method
            Expr result = rewriteInlinedBody(c.position(), decl.returnType().type(), decl.formals(), decl.body(), thisArg, thisForm, c.arguments());
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
            return result;
        } catch (InternalCompilerError e) {
            throw new InternalCompilerError("Error while inlining " +c, c.position(), e);
        }
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
        
        // get inline candidate
        X10MethodDef candidate = ((X10MethodInstance) call.methodInstance()).x10Def();
        
        // determine whether annotations suggest inlining, and
        Set<Type> callAnnotations = getExprAnnotations(call);
        if (callAnnotations.contains(NoInlineType)) 
            return null;
        
        // require inlining if either the call of the candidate are so annotated
        Boolean inliningRequired = callAnnotations.contains(InlineType) || !candidate.annotationsMatching(InlineType).isEmpty();
        
        // unless required, skip candidates previously found to be uninlinable
        if (!inliningRequired && dontInline.contains(candidate)) return null;
        
        // unless required, don't inlin
        if (!inliningRequired && !candidate.annotationsMatching(NoInlineType).isEmpty()) {
            dontInline.add(candidate);
            return null;
        }
        
        // see if the declaration for this candidate has already been cached
        X10MethodDecl decl = def2decl.get(candidate);
        if (null != decl) return decl;
        
        // get container and declaration for inline candidate
        X10ClassDef container = getContainer(candidate);
        if (null == container || isVolatileOrNative(candidate, container))  {
            if (inliningRequired) Warnings.issue(this.job, "Unable to inline " + call, call.position());
            dontInline.add(candidate);
            return null;
        }
        Job job = getJob(candidate, container);
        if (null == job) {
            if (inliningRequired) Warnings.issue(this.job, "Unable to inline " + call, call.position());
            dontInline.add(candidate);
            return null;
        }
        
        decl = getDeclaration(candidate, job.ast());
        if (null == decl) {
            if (inliningRequired) Warnings.issue(this.job, "Unable to inline " + call, call.position());
            dontInline.add(candidate);
            return null;
        }
        
        // decide whether to inline candidate
        if (!inliningRequired && (!x10.Configuration.INLINE_SMALL_METHODS || SMALL_METHOD_MAX_SIZE < getCost(decl, job) )) {
            dontInline.add(candidate);
            return null;
        }
        
        // remember what to inline this candidate with if we ever see it again
        def2decl.put(candidate, decl);
        return decl;
    }

    private ClassType EA = null;
    /**
     * Get the annotations associated with a given expression.
     * Note: it does not seem to be possible to annotate a call to void method.
     * 
     * @param expr the expression, possibly with annotations
     * @return the annotations associated with expr
     */
    private Set<Type> getExprAnnotations(Expr expr) { 
        // TODO: get the annotations on an expression correctly
        // allow annotations to void calls
        try {
            Set<Type> types = new HashSet<Type>();
            if (! (expr.ext() instanceof X10Ext)) {
                return types;
            }
            if (null == EA) EA = (ClassType) xts.systemResolver().find(QName.make("x10.lang.annotations.ExpressionAnnotation"));
            List<AnnotationNode> annotations = ((X10Ext) expr.ext()).annotations();
            for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
                AnnotationNode a = i.next(); 
                X10ClassType at = a.annotationInterface();
                if (! at.isSubtype(EA, context)) {
                    throw new InternalCompilerError("Annotations on expressions must implement " + EA, expr.position());
                }
                types.add(at);
            }
            return types;
        } catch (SemanticException e) {
            throw new InternalCompilerError("Unable to resolve ExpressionAnnotation", e);
        }
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
    private boolean isVolatileOrNative(X10MethodDef candidate, X10ClassDef container) {
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
        Compiler compiler      = job.compiler(); 
        Scheduler scheduler    = job.extensionInfo().scheduler();
        Goal goal              = scheduler.currentGoal();
        // FIXME: TEMPRORARY Inliner hack: Errors during speculative compilation for inlining should not be fatal
        Goal.Status savedState = goal.state();
        ErrorQueue  savedQueue = compiler.swapErrorQueue(new SimpleErrorQueue());
        Position pos           = candidate.position();
        Job job                = container.job();
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
            } else if (job != this.job()) {
                // TODO reconstruct the AST for the job will all preliminary compiler passes
                job.ast(job.ast().visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin())); 
            }
        } catch (Exception e) {
            Errors.issue(job, new SemanticException("AST for inline candidate job, " +job+ ", does not typecheck (" +e+ ")"));
            // e.printStackTrace();
            job = null;
        }
        ErrorQueue speculativeQueue = compiler.swapErrorQueue(savedQueue);
        if (0 < speculativeQueue.errorCount()) {
            speculativeQueue.flush();
            Warnings.issue(this.job, "WARNING: speculative compilation Errors ignored while trying to inline " +candidate, pos);
            scheduler.clearFailed();
            goal.update(savedState);
            /*
            if (decl != null) { // TODO: this may not be necessary
                Warnings.issue(this.job, "Discarding suspect decl: " +decl, c.position());
                decl = null;
            }
             */
            // System.err.println("\tdef: \t" +def);
            // System.err.println("\tpos: \t" +c.position());
            // System.err.println("\tcall:\t" +c);
            // System.err.println();
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
    return ice.getCost(decl, job);
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
        Type thisType = instantiate(mi, mi.def().container().get());
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

/*
    private Type instantiate(X10MethodInstance mi, ParameterType t) {
        List<Ref<? extends Type>> mParms = mi.x10Def().typeParameters();
        for (int i=0; i< mParms.size(); i++) {
            if (t.typeEquals(((Ref<? extends Type>) mParms.get(i)).get(), context))
                return mi.typeParameters().get(i);
        }
        List<ParameterType> cParms = ((X10ClassType) mi.container()).x10Def().typeParameters();
        for (int i=0; i<cParms.size(); i++) {
            if (t.typeEquals(cParms.get(i), context))
                return ((X10ClassType) mi.container()).typeArguments().get(i);
        }
        throw new InternalCompilerError("Type parameter " +t+ " not instantiated at call to method " +mi);
    }
*/

    public Type instantiate(X10MethodInstance mi, ParameterType genericType) {
        if (null != typeMap) {
            Type type = typeMap.reinstantiateType(genericType);
            TypeParamSubst localTypeMap = typeMap;
            if (!type.typeEquals(genericType, context()))
                return type;
            return type;
        }
        Type concreteType = instantiate(getGenericTypes(mi.x10Def().typeParameters()), mi.typeParameters(), genericType);
        if (null != concreteType)
            return concreteType;
        X10ClassType container = (X10ClassType) mi.container();
        concreteType = instantiate(container.x10Def().typeParameters(), container.typeArguments(), genericType);
        if (null != concreteType)
            return concreteType;
        throw new InternalCompilerError("Type parameter " +genericType+ " not instantiated at call to method " +mi);
    }

    /**
     * @param methodGenerics
     * @return
     */
    private List<ParameterType> getGenericTypes( List<Ref<? extends Type>> methodGenerics) {
        List<ParameterType> params = new ArrayList<ParameterType>();
        for (Ref<? extends Type> t : methodGenerics){
            params.add((ParameterType) t.get());
        }
        return params;
    }

    /**
     * @param genericTypes
     * @param concreteTypes
     * @param genericType
     */
    private Type instantiate(List<ParameterType> genericTypes, List<Type> concreteTypes, ParameterType genericType) {
        for (int i=0; i<genericTypes.size(); i++) {
            if (genericType.typeEquals(genericTypes.get(i), context)) {
                return concreteTypes.get(i);
            }
        }
        return null;
    }

    private Type instantiate(X10MethodInstance mi, ConstrainedType t) {
        return X10TypeMixin.constrainedType(instantiate(mi, Types.get(t.baseType())), Types.get(t.constraint()));
    }

    private Type instantiate(X10MethodInstance mi, X10ParsedClassType t) { // TODO
        List<Type> initialTypes = t.typeArguments();
        if (null != initialTypes) {
            List<Type> finalTypes = new ArrayList<Type>();
            for (Type type : initialTypes) {
                finalTypes.add(instantiate(mi, type));
            }
            return t.typeArguments(finalTypes);
        }
        return (Type) t.copy();
    }

    private Type instantiate(X10MethodInstance mi, Type t) {
        if (t instanceof ConstrainedType) {
            return instantiate(mi, (ConstrainedType) t);
        } else if (t instanceof ParameterType) {
            return instantiate(mi, (ParameterType) t);
        } else if (t instanceof X10ParsedClassType) {
            return instantiate(mi, (X10ParsedClassType) t); // T
        }
        assert (!(t instanceof MacroType)) : "OOPS: Typedef found after typechecking: " +t;
        assert (!(t instanceof UnknownType)): "OOPS: UnknownType: " +t;
        assert (!(t instanceof FunctionType)): "OOPS: Closure: " +t; // TODO: handle this case
        assert (!(t instanceof ConstructorInstance)): "OOPS: Constructor instance " +t; // TODO: handle this case
        assert (!(t instanceof ClosureInstance)): "OOPS: Closure instance: " +t; // TODO: handle this case
        assert (!(t instanceof AnnotatedType)): "OOPS: Annotated type : " +t; // TODO: handle this case (instantiate base class)
        return (Type) t.copy();
    }

    private X10MethodInstance instantiate(X10MethodInstance mi, X10MethodInstance methodInstance) {
        X10MethodInstance resultInstance = (X10MethodInstance) methodInstance.copy();
        resultInstance = methodInstance.returnType(instantiate(mi, resultInstance.returnType()));
        List<Type> formalTypes = new ArrayList<Type>();
        for (Type f : methodInstance.formalTypes()) {
            Type type = instantiate(mi, f);
            formalTypes.add(type);
        }
        resultInstance = (X10MethodInstance) resultInstance.formalTypes(formalTypes);
        List<Type> throwTypes = new ArrayList<Type>();
        for (Type t :  methodInstance.throwTypes()) {
            Type type = instantiate(mi, t);
            throwTypes.add(type);
        }
        resultInstance = (X10MethodInstance) resultInstance.throwTypes(throwTypes);
        // TODO: handle offer type(s)
        return resultInstance;
    }

    private X10ConstructorInstance instantiate(X10MethodInstance mi, X10ConstructorInstance constructorInstance) {
        X10ConstructorInstance resultInstance = (X10ConstructorInstance) constructorInstance.copy();
        resultInstance = constructorInstance.returnType(instantiate(mi, resultInstance.returnType()));
        List<Type> formalTypes = new ArrayList<Type>();
        List<Ref<? extends Type>> typeParameters = new ArrayList<Ref<? extends Type>>();
        for (Type f : constructorInstance.formalTypes()) {
            Type type = instantiate(mi, f);
            formalTypes.add(type);
            typeParameters.add(Types.ref(type));
        }
        resultInstance = (X10ConstructorInstance) resultInstance.formalTypes(formalTypes);
        List<Type> throwTypes = new ArrayList<Type>();
        for (Type t :  constructorInstance.throwTypes()) {
            Type type = instantiate(mi, t);
            throwTypes.add(type);
        }
        resultInstance = (X10ConstructorInstance) resultInstance.throwTypes(throwTypes);
        // TODO: handle offer type(s)
        return resultInstance;
    }

    private X10FieldInstance instantiate(X10MethodInstance mi, X10FieldInstance fieldInstance) {
        return fieldInstance.type(instantiate(mi, fieldInstance.type()));
    }

    /**
     * @param mi
     * @param constructorInstance
     * @return
     */
    protected ConstructorInstance instantiate(X10MethodInstance mi, ConstructorInstance constructorInstance) {
        X10ConstructorInstance resultInstance = (X10ConstructorInstance) constructorInstance.copy();
        List<Type> formalTypes = new ArrayList<Type>();
        for (Type f : constructorInstance.formalTypes()) {
            Type type = instantiate(mi, f);
            formalTypes.add(type);
        }
        resultInstance = (X10ConstructorInstance) resultInstance.formalTypes(formalTypes);
        List<Type> throwTypes = new ArrayList<Type>();
        for (Type t :  constructorInstance.throwTypes()) {
            Type type = instantiate(mi, t);
            throwTypes.add(type);
        }
        resultInstance = (X10ConstructorInstance) resultInstance.throwTypes(throwTypes);
        // TODO: handle offer type(s)???
        return resultInstance;
    }

    private X10MethodDecl instantiate(final X10MethodDecl decl, X10Call c) {
        final X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
        typeMap = makeTypeMap(mi);
        TypeParamSubst localTypeMap = typeMap; // DEBUG
        final HashMap<Name, LocalDef> vars = new HashMap<Name, LocalDef>();
        return (X10MethodDecl) decl.visit(new ContextVisitor(job, ts, nf) {
            protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof TypeNode) {
                    //return ((TypeNode) n).typeRef(Types.ref(instantiate(mi, ((TypeNode) n).type())));
                    TypeNode node = ((TypeNode) n).typeRef(typeMap.reinstantiateRef(((TypeNode) n).typeRef()));
                    if (!node.type().typeEquals(((TypeNode) n).type(), context()))
                        return node;
                    return node;
                }
                if (n instanceof Expr) {
                    // Expr e = ((Expr) n).type(instantiate(mi, ((Expr) n).type()));
                    Expr d = (Expr) n;
                    Expr e = d.type(typeMap.reinstantiateType(d.type()));
                    if (e instanceof Local) {
                        Local l = (Local) e;
                        LocalDef ld = vars.get(l.name().id());
                        if (ld != null) {
                            Local reins = l.localInstance(typeMap.reinstantiateLI((X10LocalInstance) l.localInstance()));
                            Local local = l.localInstance(ld.asInstance());
                            if (local.type().typeEquals(reins.type(), context())) {
                                LocalInstance rli = reins.localInstance();
                                LocalInstance lli = local.localInstance();
                                if (lli.name().equals(rli.name())) {
                                    if (lli.def().equals(rli.def())) 
                                        return reins;
                                    return local;
                                }
                                return local;
                            }
                            return local;
                        }
                        return l;
                        // return l.localInstance(typeMap.reinstantiateLI((X10LocalInstance) l.localInstance()));
                    } else if (e instanceof Field) {
                        Field f = (Field) e;
                        f = f.targetImplicit(false);
                        // f = f.fieldInstance(instantiate(mi, (X10FieldInstance) f.fieldInstance()));
                        // // f = (Field) f.typeCheck(this); // can we do this ??
                        // return f;
                        return f.fieldInstance(typeMap.reinstantiateFI((X10FieldInstance) f.fieldInstance()));
                    } else if (e instanceof Call) {
                        X10Call c = (X10Call) e;
                        c = (X10Call) c.targetImplicit(false);
                        // c = (X10Call) c.methodInstance(instantiate(mi, (X10MethodInstance) c.methodInstance()));
                        // // c = (X10Call) c.typeCheck(this); // can't do this since we inline method that refer to private fields
                        // return c;
                        return c.methodInstance(typeMap.reinstantiateMI((X10MethodInstance) c.methodInstance()));
                    } else if (e instanceof New) {
                        X10New w = (X10New) n;
                        // w = (X10New) w.type(instantiate(mi, w.type()));
                        // w = (X10New) w.constructorInstance(instantiate(mi, (X10ConstructorInstance) w.constructorInstance()));
                        // // w = (X10New) w.typeCheck(this); // can't do this since we inline method that refer to private fields
                        // return w;
                        w = (X10New) w.type(typeMap.reinstantiateType(w.type()));
                        return w.constructorInstance(typeMap.reinstantiateCI((X10ConstructorInstance) w.constructorInstance()));
                    } else if (e instanceof ClosureCall) {
                        ClosureCall c = (ClosureCall) e;
                        // c = c.closureInstance(instantiate(mi, c.closureInstance()));
                        // assert (false) : "Not yet implemented.";
                        // // c = (X10ClosureCall) c.typeCheck(this); // can't do this since we inline method that refer to private fields
                        // return c;
                        return c.closureInstance(typeMap.reinstantiateMI(c.closureInstance()));
                    } else if (e instanceof X10ConstructorCall) {
                        X10ConstructorCall c = (X10ConstructorCall) n;
                        // c = (X10ConstructorCall) c.constructorInstance(instantiate(mi, c.constructorInstance()));
                        // assert (false) : "Not yet implemented.";
                        // // x = (X10ConstructorCall) x.typeCheck(this); // can't do this since we inline method that refer to private fields
                        // return c;
                        return c.constructorInstance(typeMap.reinstantiateCI((X10ConstructorInstance) c.constructorInstance()));
                    } else if (e instanceof SettableAssign) {
                        SettableAssign a = (SettableAssign) n;
                        // a = (SettableAssign) a.type(instantiate(mi, a.type()));
                        // assert (false) : "Not yet implemented, can't instantiate " +e;
                        // return a;
                        a = (SettableAssign) a.type(typeMap.reinstantiateType(a.type()));
                        a = a.methodInstance(typeMap.reinstantiateMI((X10MethodInstance) a.methodInstance()));
                        return a.applyMethodInstance(typeMap.reinstantiateMI((X10MethodInstance) a.applyMethodInstance()));
                    } else if (e instanceof FieldAssign) {
                        X10FieldAssign_c f = (X10FieldAssign_c) n;
                        // f = (X10FieldAssign_c) f.type(instantiate(mi, f.type()));
                        // assert (false) : "Not yet implemented, can't instantiate " +e;
                        // return f;
                        f = (X10FieldAssign_c) f.type(typeMap.reinstantiateType(f.type()));
                        return f.fieldInstance(typeMap.reinstantiateFI((X10FieldInstance) f.fieldInstance()));
                    } else if (e instanceof AssignPropertyBody) {
                        AssignPropertyBody b = (AssignPropertyBody) n;
                        assert (false) : "Not yet implemented, can't instantiate " +e;
                        // TODO: ASK IGOR: how to handle AssignPropertyBody
                        return b;
                    } else if (e instanceof Special) {
                        if (((Special) e).kind().equals(Special.SUPER)) {
                            assert (false) : "Not yet implemented, can't instantiate " +e;
                        }
                        return e;
                    }
                    return e;
                }
                if (n instanceof LocalDecl) {
                    LocalDecl d = (LocalDecl) n;
                    boolean sigChanged = d.type() != ((LocalDecl) old).type(); // conservative compare detects changes in substructure
                    if (sigChanged) {
                        LocalDef ld = d.localDef();
                        Name name = ld.name();
                        LocalDef ild = ts.localDef(ld.position(), ld.flags(), d.type().typeRef(), name);
                        vars.put(name, ild); // FIXME: scoping // TODO: understand this issue
                        return d.localDef(ild);
                    }
                    return d;
                }
                if (n instanceof Formal) {
                    Formal f = (Formal) n;
                    boolean sigChanged = f.type() != ((Formal) old).type(); // conservate compare detects changes in substructure
                    if (sigChanged) {
                        LocalDef ld = f.localDef();
                        Name name = ld.name();
                        LocalDef ild = ts.localDef(ld.position(), ld.flags(), f.type().typeRef(), name);
                        vars.put(name, ild);
                        return f.localDef(ild);
                    }
                    return f;
                }
                if (n instanceof ClassDecl) {
                    ClassDecl d = (ClassDecl) n;
                    boolean sigChanged = d.superClass() != ((ClassDecl) old).superClass();
                    List<TypeNode> interfaces = d.interfaces();
                    List<TypeNode> oldInterfaces = ((ClassDecl) old).interfaces();
                    for (int i = 0; i < interfaces.size(); i++) {
                        sigChanged |= interfaces.get(i) != oldInterfaces.get(i);
                    }
                    if (sigChanged) {
                        throw new InternalCompilerError("Inlining of code with instantiated local classes not supported");
                    }
                    return d;
                }
                if (n instanceof FieldDecl) {
                    FieldDecl d = (FieldDecl) n;
                    assert (false) : "Not yet implemented, can't instantiate " +n;
                    return d;
                }
                if (n instanceof Closure) {
                    Closure c = (Closure) n;
                    assert (false) : "Not yet implemented, can't instantiate " +n;
                    return c;
                }
                if (n instanceof ConstructorDecl) {
                    ConstructorDecl c = (ConstructorDecl) n;
                    assert (false) : "Not yet implemented, can't instantiate " +n;
                    return c;
                }
                if (n instanceof X10MethodDecl) {
                    X10MethodDecl d = (X10MethodDecl) n;
                    boolean sigChanged = d.returnType() != ((X10MethodDecl) old).returnType();
                    List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
                    List<LocalDef> formalNames = new ArrayList<LocalDef>();
                    List<Formal> params = d.formals();
                    List<Formal> oldParams = ((X10MethodDecl) old).formals();
                    for (int i = 0; i < params.size(); i++) {
                        Formal p = params.get(i);
                        sigChanged |= p != oldParams.get(i);
                        argTypes.add(p.type().typeRef());
                        formalNames.add(p.localDef());
                    }
                    sigChanged |= d.guard() != ((X10MethodDecl) old).guard();
                    List<Ref <? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
                    SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(typeSystem()) : d.exceptions();
                    SubtypeSet oldExcs = ((X10MethodDecl) old).exceptions();
                    if (null != excs) {
                        for (Type et : excs) {
                            sigChanged |= !oldExcs.contains(et);
                            excTypes.add(Types.ref(et));
                        }
                    }
                    sigChanged |= d.offerType() != ((X10MethodDecl) old).offerType();
                    if (sigChanged) {
                        X10MethodDef md = (X10MethodDef) d.methodDef();
                        DepParameterExpr g = d.guard();
                        TypeNode ot = d.offerType();
                        X10MethodDef imd = xts.methodDef(md.position(), md.container(), md.flags(), d.returnType().typeRef(),
                                                     md.name(), md.typeParameters(), argTypes, md.thisVar(), formalNames,
                                                     g == null ? null : g.valueConstraint(),
                                                     g == null ? null : g.typeConstraint(), excTypes,
                                                     ot == null ? null : ot.typeRef(), null /* the body will never be used */);
                        return d.methodDef(imd);
                    }
                    return d;
                }
                return n;
            }
        }.context(context()));
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
        typeParms.addAll(getGenericTypes(method.x10Def().typeParameters()));
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
            if (n instanceof X10Call)
                return visitCall((X10Call)n);
            if (n instanceof Special)
                return visitSpecial((Special)n);
            return n;
        }
        private Block rewriteBody(Position pos, Block body) throws SemanticException {
            if (label == null) return body;
            X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
            X10TypeSystem xts = (X10TypeSystem) typeSystem();
            List<Stmt> newBody = new ArrayList<Stmt>();
            if (ret != null) {
                newBody.add(xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                            xnf.CanonicalTypeNode(pos, ret.type()),
                            xnf.Id(pos, ret.name())).localDef(ret));
            }
            newBody.add(xnf.Labeled( pos, 
                                     xnf.Id(pos, label),
                                     xnf.Do(pos, body, syn.createFalse(pos)) )); 
            if (ret != null) {
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
        }
        public final void visit(Node n) {
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
