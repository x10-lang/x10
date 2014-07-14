/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.optimizations.inlining;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
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
import polyglot.ast.Variable;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.MemberInstance;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.InlinableCall;
import x10.ast.ParExpr;
import x10.ast.StmtExpr;
import x10.ast.X10ClassDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10.errors.Warnings;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10LocalDef;
import x10.types.X10ProcedureDef;
import x10.types.constants.ConstantValue;
import x10.types.constants.StringValue;
import x10.types.constraints.ConstraintManager;
import x10.util.AltSynthesizer;
import x10.util.AnnotationUtils;
import x10.visit.ConstantPropagator;
import x10.visit.ExpressionFlattener;
import x10.visit.NodeTransformingVisitor;
import x10.visit.X10AlphaRenamer;
import x10cuda.ast.CUDAKernel;

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
 * @author Bowen Alpern
 */
@SuppressWarnings("unchecked")
public class Inliner extends ContextVisitor {
    
    static final boolean DEBUG = false;
    
    private static final int VERBOSITY = 0;
    
    private static final int INITIAL_RECURSION_DEPTH =  0;
    private static final int RECURSION_DEPTH_LIMIT   =  2; // max number of calls to the same procedure to be inlined at once
    private static final int INLINE_DEPTH_LIMIT      = 10; // max number of calls to be inlined at once

    private boolean inliningRequired; // Should the current call be inlined no matter how big it is?

    /**
     * Explicit inlining (via annotations) and inlining of small methods (if
     * enabled) is done to an arbitrary depth for non-recursive calls. There is
     * a somewhat hoaky mechanism for limiting the number of recursive calls
     * that are inlined.
     * 
     * TODO: implement a space budget based policy mechanism for controling inlining. 
     */
    private final Stack<String> inlineInstances = new Stack<String>();
    private final int recursionDepth[] = new int[1]; // number of calls to the same procedure currently being inlined
    private final AltSynthesizer syn;
    private DeclStore repository;
    private InlineUtils utils;
    private final boolean closuresOnly;

    /**
     * Create a new Inliner.
     * 
     * @param job the Job whose procedures (methods, constructors, and closures) are to have their procedure calls inlined
     * @param ts the current TypeSystem
     * @param nf the current NodeFactory
     * @param closuresOnly are we running a restricted inliner that is just cleaning up apply on literal closures?
     */
    public Inliner(Job job, TypeSystem ts, NodeFactory nf, boolean closuresOnly) {
        super(job, ts, nf);
        syn                   = new AltSynthesizer(ts, nf);
        ExtensionInfo extInfo = (ExtensionInfo) job.extensionInfo();
        Configuration config  = ((X10CompilerOptions) extInfo.getOptions()).x10_config;
        this.closuresOnly = closuresOnly;
    }

    /**
     * Initialize this Inliner.  
     * Some of this initialization cannot be done in the constructor or in would introduce cycles in the schedule.
     * 
     * @see polyglot.visit.ContextVisitor#begin()
     */
    @Override
    public NodeVisitor begin() {
        if (!closuresOnly) {
            repository        = job.compiler().getInlinerData(job, ts, nf);
        }
        utils             = new InlineUtils(job);
        recursionDepth[0] = INITIAL_RECURSION_DEPTH;
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
    @Override
    public Node override(Node node) {
        Position pos = node.position(); // DEBUG
        if (2 <= VERBOSITY && node instanceof SourceFile) {
            Source s = ((SourceFile) node).source();
            Warnings.issue(this.job, "(begin inlining)", pos);
        }
        if (ExpressionFlattener.cannotFlatten(node, job)) { // TODO: check that flattening is actually required
            if (DEBUG) debug("Cannot flatten: short-circuiting inlining for children of " + node, node);
            return node; // don't inline inside Nodes that cannot be Flattened
        }
        if (node instanceof X10MethodDecl) {
            return null; // @NoInline annotation means something else
        }
        if (node instanceof InlinableCall) {
            return null; // will handle @NoInline annotation seperately
        }
        if (node instanceof CUDAKernel) {
            return node;  // disable inlining within a CUDAKernal XTENLANG-2824.  
        }
        if (node instanceof ClassDecl) { // Don't try to inline native classes
            if (utils.inliningProhibited(((X10ClassDecl) node).classDef())) {
                return node;
            }
        }
        if (utils.inliningProhibited(node)) { // allows whole AST's to be ignored (primarily DEBUG only (remove this)
            if (DEBUG) debug("Explicit @NoInline annotation: short-circuiting inlining for children of " + node, node);
            return node;
        }
        return null;
    }

    /**
     * Rewrite procedure (method, constructor, closure) call nodes, inlining the body of the callee.
     * 
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!closuresOnly && n instanceof X10MethodDecl && AnnotationUtils.hasAnnotation(((X10MethodDecl) n).methodDef(), utils.InlineOnlyType))
            return null;
        if (!(n instanceof ProcedureCall))
            return n;
        Position pos = n.position();
        inliningRequired = false;
        reasons.clear();
        Node result = null;
        if (n instanceof ClosureCall) {
            if (4 <= VERBOSITY)
                Warnings.issue(job, "? inline level " +inlineInstances.size()+ " closure " +n, pos);
            result = inlineClosureCall((ClosureCall) n);
        } else if (!closuresOnly && n instanceof InlinableCall) {
            result = inlineConstant((InlinableCall) n);
            if (null != result) 
                return result;
            if (4 <= VERBOSITY)
                Warnings.issue(job, "? inline level " +inlineInstances.size()+ " call " +n, pos);
            result = inlineCall((InlinableCall) n);
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
        if (n instanceof ConstructorCall && result instanceof StmtExpr) { // evaluate the expr // method calls in Stmt context are already sowrapped
            result = syn.createEval((StmtExpr) result);
        }
        if (n != result && 3 <= VERBOSITY)
            Warnings.issue(job, "INLINING: " +n+ " (level " +inlineInstances.size()+ ")", pos);
        return result;
    }

    private Expr inlineClosureCall(ClosureCall c) {
        Closure lit = getClosure(c.target());
        if (null == lit) {
            report("of non literal closure call target " +c.target(), c);
            return null;
        }
        lit = (Closure) instantiate(lit, c);
        if (null == lit) {
            report("of failure to instatiate closure", c);
            return null;
        }
        lit = (Closure) lit.visit(new X10AlphaRenamer(this));
        // Ensure that the last statement of the body is the only return in the closure
        Block body = InliningRewriter.rewriteClosureBody(lit, job(), context());
        if (null == body) {
            report("normalized closure has no body", c);
            return null;
        }
        List<Expr> args = new ArrayList<Expr>();
        int i = 0;
        for (Expr a : c.arguments()) {
            args.add(syn.createUncheckedCast(a.position(), a, c.closureInstance().formalTypes().get(i++), context()));
        }
        Expr result = rewriteInlinedBody(c.position(), lit.returnType().type(), lit.formals(), body, null, args);
        if (null == result) {
            report("of failure to rewrite closure body", c);
            return null;
        }
        result = (Expr) result.visit(this);
        return result;
    }

    private Closure getClosure(Expr target) {
        if (target instanceof Closure)
            return (Closure) target;
        if (target instanceof ParExpr)
            return getClosure(((ParExpr) target).expr());
        return null;
    }

    /**
     * @param call
     * @return
     */
    private Node inlineConstant(InlinableCall call) {
        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) job().extensionInfo();
        x10Info.stats.startTiming("ConstantPropagator", "ConstantPropagator");
        try {
            X10ProcedureDef def = repository.getDef(call);
            Type type = def.returnType().get();
            List<Type> a = AnnotationUtils.getAnnotations(def, utils.ConstantType);
            X10CompilerOptions opts = (X10CompilerOptions) job.extensionInfo().getOptions();
            ConstantValue cv = extractCompileTimeConstant(type, a, opts, context);
            if (cv == null) return null;
            Expr literal = cv.toLit(nf, ts, type, call.position());
            return literal;
        }
        finally {
            x10Info.stats.stopTiming();
        }
    }

    public static ConstantValue extractCompileTimeConstant(Type type, List<? extends Type> annotations, X10CompilerOptions opts, Context context) {
        if (annotations.isEmpty()) return null;
        TypeSystem ts = type.typeSystem();
        Expr arg = ((X10ClassType) annotations.get(0)).propertyInitializer(0);
        if (!arg.isConstant() || !arg.type().isSubtype(ts.String(), context)) 
            return null;
        String name = ((StringValue) arg.constantValue()).value();
        Boolean negate = name.startsWith("!"); // hack to allow @CompileTimeConstant("!NO_CHECKS")
        if (negate) {
            assert ts.isBoolean(type);
            name = name.substring(1);
        }
        try {
            Object value = opts.x10_config.get(name);
            ConstantValue cv = ConstantValue.make(type, negate ? ! (Boolean) value : value);
            return cv;
        } catch (ConfigurationError e) {
            return null;
        } catch (OptionError e) {
            return null;
        }
    }

    private Expr inlineCall(InlinableCall call) {
        if (utils.inliningProhibited(call)) {
            report("of annotation at call site", call);
            return null;
        }
        ProcedureDecl decl = repository.retrieveDecl(call);
        if (null == decl) return null;
        String signature = makeSignatureString(decl);
        decl = (ProcedureDecl) instantiate(decl, call);
        if (null == decl) {
            report("instantiation failure for " + signature, call);
            return null;
        }
        decl = (ProcedureDecl) decl.visit(new X10AlphaRenamer(this));
        Expr result = rewriteDecl(call, decl);
        if (null == result) {
            report("cannot rewrite decl", call);
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

        // TODO: tell context that the place of result is the same as that of its surrounding context
        return result;
    }

    /**
     * @param call
     * @param decl
     * @return
     */
    public Expr rewriteDecl(InlinableCall call, ProcedureDecl decl) {
        LocalDecl thisArg = null;
        LocalDef thisDef = null;
        if (null != call.target() && call.target() instanceof Expr) {
            Expr target = (Expr) call.target();
            if (target instanceof Local) {
                thisDef = ((Local) target).localInstance().def();
            } else {
                if (call instanceof ConstructorCall) {
                    Type type = ((ConstructorCall) call).constructorInstance().returnType();
                    if (ts.isStruct(type)) {
                        report("cannot inline constructor call with non Local target for struct " + type, call);
                        return null;
                    }
                }
                thisArg = reifyThis(call);
                thisDef = thisArg.localDef();
            }
        }
        Block body = InliningRewriter.rewriteProcedureBody(decl, thisDef, job(), context());
        if (null == body) {
            return null;
        }
        Expr result = rewriteInlinedBody( call.position(),
                                          decl instanceof MethodDecl ? ((MethodDecl) decl).returnType().type() : null,
                                          decl.formals(),
                                          body,
                                          thisArg,
                                          call.arguments() );
        return result;
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

    private Node propagateConstants(Node n) {
        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) job().extensionInfo();
        x10Info.stats.startTiming("ConstantPropagator.context", "ConstantPropagator.context");
        Node retNode = n.visit(new ConstantPropagator(job, ts, nf, true).context(context()));
        x10Info.stats.stopTiming();
        return retNode;
    }

    private LocalDecl reifyThis(InlinableCall call) {
        assert call.target() instanceof Expr;
        Expr target = (Expr) call.target();
        Position position = target.position();
        if (target instanceof Special && ((Special) target).kind() == Special.SUPER) {
            target = rewriteSuperAsThis((Special) target);
        }
        
        ProcedureInstance<? extends ProcedureDef> pi = call instanceof Call ? ((Call) call).procedureInstance() : ((ConstructorCall) call).procedureInstance();
        Type type = ((MemberDef) pi.def()).container().get();
        type = makeTypeMap(pi, call.typeArguments()).reinstantiate(type);
        
        Expr expr = syn.createUncheckedCast(position, target, type, context());
        LocalDecl thisDecl = syn.createLocalDecl(call.position(), Flags.FINAL, Name.makeFresh("this"), expr);
        return thisDecl;
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

    private CodeBlock instantiate(final CodeBlock code, InlinableCall call) {
        try {
            if (DEBUG) debug("Instantiate " + code, call);
            TypeParamSubst typeMap = makeTypeMap(call.procedureInstance(), call.typeArguments());
            InliningTypeTransformer transformer =
                new InliningTypeTransformer(typeMap, context().currentCode(), (X10CodeDef) code.codeDef(), call.position());
            ContextVisitor visitor = new NodeTransformingVisitor(job, ts, nf, transformer).context(context());
            CodeBlock visitedCode = (CodeBlock) code.visit(visitor);
            return visitedCode;
        } catch (Throwable e) {
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

    private Expr rewriteInlinedBody(Position pos, Type retType, List<Formal> formals, Block body, LocalDecl thisDecl, List<Expr> args) {
        List<Stmt> statements = new ArrayList<Stmt>();

        // declare locals for 'this', if there is one, and any formal parameters
        if (thisDecl != null) { // declare temporary for "this" arg
            statements.add(thisDecl);
        }
        assert (args.size() == formals.size());
        for (int i = 0; i < args.size(); i++) {
            Expr arg = args.get(i);
            X10Formal formal = (X10Formal) formals.get(i);
            X10LocalDef localDef = formal.localDef();
            Expr expr = syn.createUncheckedCast(arg.position(), arg, formal.type().type(), context());
            localDef.setType(Types.ref(expr.type()));
            LocalDecl ld = syn.createLocalDecl(formal, expr);           
            tieLocalDefToItself(ld.localDef());
            statements.add(ld);
        }
        
        // Create statement expression from body.
        // body is in normal form:
        // 1) last statement in body is a return statement, and
        // 2) this is the only return in the body.
        List<Stmt> bodyStmts = body.statements();
        if (!(bodyStmts.get(bodyStmts.size() - 1) instanceof Return)) {
            report("body cannot be normalized", body);
            return null;
        }
        Return ret = (Return) bodyStmts.get(bodyStmts.size() - 1);
        for (Stmt stmt : bodyStmts) {
            if (stmt != ret) {
                statements.add(stmt);
            }
        }
        Expr expr = ret.expr();
        if (null != expr) {
            assert null != retType; // null retType => constructor call body (which shouldn't return an expr)
            expr = syn.createUncheckedCast(expr.position(), expr, retType, context());
        }
        StmtExpr result = syn.createStmtExpr(pos, statements, expr);
        
        Expr result2 = (Expr)propagateConstants(result);
        
        return result2;
    }

    /**
     * @param d
     */
    private void tieLocalDefToItself(LocalDef d) {
        tieLocalDef(d, d);
    }

    private void tieLocalDef(LocalDef d, LocalDef o) {
        Type type = Types.get(d.type());
        type = Types.addSelfBinding(type, ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef) o));
        ((Ref<Type>) d.type()).update(type);
    }

    private long lastTime;
    private long timer () {
        long time = System.currentTimeMillis();
        long delta = time - lastTime;
        lastTime = time;
        return delta;
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

    private List<String> reasons = new ArrayList<String>();

    /**
     * @param msg
     * @return
     */
    String report(String msg, Node n) {
        reasons.add(msg);
        if (DEBUG) debug("not inlining because " + msg, n);
        return msg;
    }

}
