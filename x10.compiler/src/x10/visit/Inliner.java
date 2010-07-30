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
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.FunctionDef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.AlphaRenamer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.ParExpr;
import x10.ast.StmtExpr;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.util.Synthesizer;
import x10cpp.visit.X10SearchVisitor;

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

    /**
     * This constantcontrols accesses to inaccessible fields.
     */
    private static final boolean ALLOW_STMTEXPR = true;

    /**
     * Names of the annotation classes that govern inlining.
     */
    private static final QName INLINE_ANNOTATION    = QName.make("x10.compiler.Inline");
    private static final QName NO_INLINE_ANNOTATION = QName.make("x10.compiler.NoInline");

    /**
     * The cached type of the @Inline and @NoInline annotations.
     */
    private Type InlineType;
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
            NoInlineType = (Type) ts.systemResolver().find(NO_INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.out.println("Unable to find " +NO_INLINE_ANNOTATION+ ": "+e);
            NoInlineType = null;
        }
        try {
            InlineType = (Type) ts.systemResolver().find(INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.out.println("Unable to find " +INLINE_ANNOTATION+ ": "+e);
            InlineType = null;
        }
        return super.begin();
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!ALLOW_STMTEXPR) return n;  // FIXME: for now
        if (n instanceof X10Call) return inlineMethodCall((X10Call_c) n);
        if (n instanceof ClosureCall) return inlineClosureCall((ClosureCall) n);
        return n;
    }

    private Expr inlineMethodCall(X10Call_c c) {
        if (null == InlineType) return c;
        try {
            String methodId    = getContainer(((X10MethodInstance) c.methodInstance()).x10Def()).fullName()+ 
                                 "." +
                                 c.methodInstance().signature();
            X10MethodDecl decl = getInlineDecl(c);
            if (null == decl) 
                return c;
            decl = instantiate(decl, c);
            LocalDef ths = computeThis(decl.methodDef());
            LocalDecl ld = null == ths ? null : syn.createLocalDecl( ths.position(), 
                                                                     ths.flags(), 
                                                                     ths.name(), 
                                                                     ths.type().get(), 
                                                                     (Expr) c.target() ).localDef(ths);
            decl = normalizeMethod(decl, ths); // Ensure that the last statement of the body is the only return in the method
            List<Expr> args = new ArrayList<Expr>();
            int i = 0;
            for (Expr a : c.arguments()) {
                args.add(cast(a, c.methodInstance().formalTypes().get(i++)));
            }
            Expr result = rewriteInlinedBody(c.position(), decl.returnType().type(), decl.formals(), decl.body(), ld, args);
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

    private Expr inlineClosureCall(ClosureCall c) {
        Closure lit = getInlineClosure(c);
        if (null == lit) return c;
        List<Expr> args = new ArrayList<Expr>();
        int i = 0;
        for (Expr a : c.arguments()) {
            args.add(cast(a, c.closureInstance().formalTypes().get(i++)));
        }
        lit = normalizeClosure(lit); // Ensure that the last statement of the body is the only return in the closure
        Expr result = rewriteInlinedBody(c.position(), lit.returnType().type(), lit.formals(), lit.body(), null, args);
        result = (Expr) result.visit(new AlphaRenamer());
        result = (Expr) result.visit(this);
        result = (Expr) propagateConstants(result);
        return result;
    }

    /**
     * @param c
     * @return
     */
    private X10MethodDecl getInlineDecl(X10Call_c c) {
        Set<Type> annotations = getAnnotations(c);
        if (annotations.contains(NoInlineType))
            return null;
        Boolean forceInline = annotations.contains(InlineType);
        if (forceInline) 
            System.out.println("DEBUG: Success!! got expression annotation: " + annotations);
        X10MethodDef def = ((X10MethodInstance) c.methodInstance()).x10Def();
        if (!forceInline && dontInline.contains(def)) 
            return null;
        X10MethodDecl decl = def2decl.get(def);
        if (null != decl) 
            return decl;
        if (!forceInline && !def.annotationsMatching(NoInlineType).isEmpty()) {
            dontInline.add(def);
            return null;
        }
        // TODO ASK: is there an easier way to get form a def to a job (or an AST)?
        X10ClassDef container = getContainer(def);
        decl = getDeclaration(def, container);
        if (null == decl) {
            dontInline.add(def);
            return null;
        }
        if ( !forceInline && def.annotationsMatching(InlineType).isEmpty() && 
             ( !x10.Configuration.INLINE_SMALL_METHODS ||
               SMALL_METHOD_MAX_SIZE < ice.getCost(decl, container.job()) )
           ) {
            dontInline.add(def);
            return null;
        }
        def2decl.put(def, decl);
        return decl;
    }
    
    private ClassType EA = null;
    
    /**
     * @param c
     * @return
     */
    private Set<Type> getAnnotations(Expr expr) { 
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
    private X10MethodDecl normalizeMethod(X10MethodDecl decl, LocalDef ths) {
        return (X10MethodDecl) decl.visit(new InliningRewriter(decl, ths));
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
     * Cast a given expression to a given type, unless it is already of that type.
     * TODO: factor out to Synthesizer
     * @param a the given expression
     * @param fType the given type
     * @return a cast node, or the original expression
     */
    private Expr cast(Expr a, Type fType) {
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        Context context = context();
        if (!xts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            a = xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, fType), a,
                            Converter.ConversionType.UNCHECKED).type(fType);
        }
        return a;
    }

    /**
     * Create a reference to a local variable with a given def.
     * TODO: factor out to Synthesizer.
     * @param pos the position
     * @param t the given local def
     * @return the local node
     */
    private Expr local(Position pos, LocalDef t) {
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        LocalInstance li = t.asInstance();
        return xnf.Local(pos, xnf.Id(pos, li.name())).localInstance(li).type(li.type());
    }

    private LocalDef computeThis(MethodDef def) {
//        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        if (def.flags().isStatic()) return null;
        return xts.localDef(def.position(), xts.Final(), def.container(), Name.makeFresh("this"));
    }

    /**
     * Get the definition of the X10 Class that implements a given method.
     * 
     * @param md the method definition whose container is desired
     * @return the definition of the X10 Class containing md
     */
    private X10ClassDef getContainer(X10MethodDef md) {
        Type containerBase = X10TypeMixin.baseType(Types.get(md.container()));
        assert (containerBase instanceof X10ClassType);
        return ((X10ClassType) containerBase).x10Def();
    }

    private X10MethodDecl getDeclaration(final X10MethodDef md, final X10ClassDef cd) {
        if (!md.flags().isStatic() && !md.flags().isFinal() && !md.flags().isPrivate() && !cd.flags().isFinal() && !cd.isStruct())
            return null;
        Job job = cd.job();
        if (job == null) // TODO: reconstruct job from md.position()
            return null;
        NodeVisitor typeChecker = new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin();
        Node ast = job.ast();
        if (job != this.job()) {
            ast = ast.visit(typeChecker);
        }
//        job.ast(ast);
//        System.out.println("==> Typechecked "+cd);
        final X10MethodDecl[] decl = new X10MethodDecl[1];
        ast.visit(new NodeVisitor() {
            public Node override(Node n) {
                if (n instanceof Expr || n instanceof Stmt || n instanceof TypeNode) { // FIXME: for any local classes 
                    return n;
                }
                if (decl[0] != null) {
                    return n;
                }
                return null;
            }
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof X10MethodDecl) {
                    X10MethodDecl d = (X10MethodDecl) n;
                    if (d.methodDef() == md)
                        decl[0] = d;
                }
                return n;
            }
        });
        if (null == decl[0] || null == decl[0].body()) 
            return null;
        return decl[0];
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, ParameterType t) {
        List<Ref<? extends Type>> mParms = mi.x10Def().typeParameters();
        List<Type> mArgs = mi.typeParameters();
        int i = 0;
        for (Ref<? extends Type> tpr : mParms) {
            if (t.typeEquals(tpr.get(), context))
                return mArgs.get(i);
            ++i;
        }
        X10ClassType ct = (X10ClassType) mi.container();
        X10ClassDef cd = ct.x10Def();
        tArgs = ct.typeArguments();
        List<ParameterType> tParms = cd.typeParameters();
        i = 0;
        for (ParameterType pt : tParms) {
            if (t.typeEquals(pt, context))
                return tArgs.get(i);
            ++i;
        }
        return null;
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, ConstrainedType t) {
        Type bt = Types.get(t.baseType());
        Type ibt = instantiate(mi, tArgs, bt);
        if (ibt == bt) return t;
        return X10TypeMixin.constrainedType(ibt, Types.get(t.constraint()));
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, Type t) {
        if (t instanceof ConstrainedType) {
            return instantiate(mi, tArgs, (ConstrainedType) t);
        } else if (t instanceof ParameterType) {
            return instantiate(mi, tArgs, (ParameterType) t);
        }
        return t;
    }

    private X10MethodDecl instantiate(final X10MethodDecl decl, X10Call c) {
        final X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
        final List<Type> tArgs = new ArrayList<Type>();
        for (TypeNode tn : c.typeArguments()) {
            tArgs.add(tn.type());
        }
//       List<Type> tParms = mi.typeParameters();
        final HashMap<Name, LocalDef> vars = new HashMap<Name, LocalDef>();
        return (X10MethodDecl) decl.visit(new ContextVisitor(job, ts, nf) {
            protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof TypeNode) {
                    Type type = ((TypeNode) n).type();
                    Type iType = instantiate(mi, tArgs, type);
                    if (iType != type) { // conservative compare detects changes in substructure
                        return ((TypeNode) n).typeRef(Types.ref(iType));
                    } else {
                        return n;
                    }
                }
                if (n instanceof Expr) {
                    Expr e = (Expr) n;
                    Expr ie = e.type(instantiate(mi, tArgs, e.type()));
                    if (ie instanceof X10Call) {
                        X10Call c = (X10Call) ie;
                        if (c.isTargetImplicit()) {
                            c = (X10Call) c.targetImplicit(false);
                        }
                        return c.typeCheck(this); // TODO: eliminate typeCheck (allow access to private methods)
                    } else if (ie instanceof Field) {
                        Field f = (Field) ie;
                        if (f.isTargetImplicit()) {
                            f = (Field) f.targetImplicit(false);
                        }
                        return f.typeCheck(this); // TODO: eliminate typeCheck (allow access to private fields)
                    } else if (ie instanceof Local) {
                        LocalDef ld = vars.get(((Local) ie).name().id());
                        if (ld != null) {
                            return ((Local) ie).localInstance(ld.asInstance());
                        }
                    }
                    return ie;
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
                    SubtypeSet excs = d.exceptions();
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
                        X10TypeSystem xts = (X10TypeSystem) ts;
                        DepParameterExpr g = d.guard();
                        TypeNode ot = d.offerType();
                        X10MethodDef imd = xts.methodDef(md.position(), md.container(), md.flags(), d.returnType().typeRef(),
                                                     md.name(), md.typeParameters(), argTypes, md.thisVar(), formalNames,
                                                     g == null ? null : g.valueConstraint(),
                                                     g == null ? null : g.typeConstraint(), excTypes,
                                                     ot == null ? null : ot.typeRef(), null /* the body will never be used */);
                        return d.methodDef(imd);
                    }
                }
                return n;
            }
        }.context(context()));
    }

    // TODO: generate a closure call instead of a statement expression // is this still necessary?
    private Expr rewriteInlinedBody(Position pos, Type retType, List<Formal> formals, Block body, LocalDecl ths, List<Expr> args) {
        boolean clashes = false;
        for (Expr a : args) {
            X10SearchVisitor<X10Local_c> xLocals = new X10SearchVisitor<X10Local_c>(X10Local_c.class);
            a.visit(xLocals);
            if (!xLocals.found()) continue;
            ArrayList<X10Local_c> locals = xLocals.getMatches();
            for (X10Local_c t : locals) {
                Name name = t.localInstance().name();
                for (Formal f : formals) {
                    if (f.name().id().equals(name)) clashes = true;
                }
            }
        }

        List<Stmt> bodyStmts = body.statements();
        assert (bodyStmts.get(bodyStmts.size()-1) instanceof Return) : "Last statement is not a return";
        // We know that the last statement has to be a return
        Return ret = (Return) bodyStmts.get(bodyStmts.size()-1);
        List<Stmt> statements = new ArrayList<Stmt>();
        for (Stmt stmt : bodyStmts) {
            if (stmt != ret) {
                statements.add(stmt);
            }
        }
        Expr e = ret.expr();
        if (null != e)
            e = cast(e, retType);

        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        Synthesizer synth = new Synthesizer(xnf, xts);
        StmtExpr result = (StmtExpr) xnf.StmtExpr(pos, statements, e).type(retType);

        List<Stmt> declarations = new ArrayList<Stmt>();
        if (ths != null) {
            declarations.add(ths);
        }
        LocalDef[] alt = null;
        if (clashes) {
            alt = new LocalDef[args.size()];
            int i = 0;
            for (Expr a : args) {
                Type fType = formals.get(i).type().type();
                Name tmp = Name.makeFresh();
                alt[i] = xts.localDef(pos, xts.Final(), Types.ref(fType), tmp);
                declarations.add(synth.makeLocalVar(pos, alt[i], a));
                i++;
            }
        }
        int i = 0;
        for (Expr a : args) {
            Formal f = formals.get(i);
            Expr v = clashes ? local(pos, alt[i]) : a;
            declarations.add(synth.makeLocalVar(pos, f.localDef(), v));
            i++;
        }

        return result.prepend(declarations);
    }

    /**
     * Rewrites a given closure so that it has exactly one return statement at the end.
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
            newBody.add(xnf.Labeled(pos, xnf.Id(pos, label),
                        xnf.Do(pos, body, (Expr) xnf.BooleanLit(pos, false).typeCheck(this))));
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
