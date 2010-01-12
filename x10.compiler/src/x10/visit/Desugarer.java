/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary_c;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Catch;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Closure;
import x10.ast.Closure_c;
import x10.ast.DepParameterExpr;
import x10.ast.Finish;
import x10.ast.ForEach;
import x10.ast.FunctionTypeNode;
import x10.ast.Future;
import x10.ast.Here;
import x10.ast.Next;
import x10.ast.ParExpr;
import x10.ast.SettableAssign_c;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special_c;
import x10.ast.X10Unary_c;
import x10.constraint.XConstraint;
import x10.constraint.XRoot;
import x10.emitter.Emitter;
import x10.types.ClosureDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.util.ClosureSynthesizer;
import x10.util.Synthesizer;

/**
 * Visitor to desugar the AST before code gen.
 */
public class Desugarer extends ContextVisitor {
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;
    private final Synthesizer synth;
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
        synth = new Synthesizer(xnf, xts);
    }

    private static int count;

    private static Name getTmp() {
        return Name.make("__desugarer__var__" + (count++) + "__");
    }

    protected X10Context xContext() { return (X10Context) context;}
    private static final Name RUN_AT = Name.make("runAt");
    private static final Name EVAL_AT = Name.make("evalAt");
    private static final Name EVAL_FUTURE = Name.make("evalFuture");
    private static final Name RUN_ASYNC = Name.make("runAsync");
    private static final Name HERE = Name.make("here");
    private static final Name NEXT = Name.make("next");
    private static final Name LOCK = Name.make("lock");
    private static final Name AWAIT = Name.make("await");
    private static final Name RELEASE = Name.make("release");
    private static final Name START_FINISH = Name.make("startFinish");
    private static final Name PUSH_EXCEPTION = Name.make("pushException");
    private static final Name STOP_FINISH = Name.make("stopFinish");
    private static final Name APPLY = Name.make("apply");
    private static final Name SET = Name.make("set");
    private static final Name CONVERT = X10Cast_c.operator_as;
    private static final Name CONVERT_IMPLICITLY = X10Cast_c.implicit_operator_as;
    private static final Name DIST = Name.make("dist");

    public Node override(Node parent, Node n) {
        if (n instanceof Eval) {
            try {
                Stmt s = visitEval((Eval) n);
                return visitEdgeNoOverride(parent, s);
            }
            catch (SemanticException e) {
                return null;
            }
        }

        return null;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Future)
            return visitFuture((Future) n);
        if (n instanceof Async)
            return visitAsync(old, (Async) n);
        if (n instanceof AtStmt)
            return visitAtStmt((AtStmt) n);
        if (n instanceof AtExpr)
            return visitAtExpr((AtExpr) n);
        if (n instanceof Here)
            return visitHere((Here) n);
        if (n instanceof Next)
            return visitNext((Next) n);
        if (n instanceof Atomic)
            return visitAtomic((Atomic) n);
        if (n instanceof Await)
            return visitAwait((Await) n);
        if (n instanceof When)
            return visitWhen((When) n);
        if (n instanceof Finish)
            return visitFinish((Finish) n);
        if (n instanceof ForEach)
            return visitForEach((ForEach) n);
        if (n instanceof AtEach)
            return visitAtEach((AtEach) n);
        if (n instanceof Eval)
            return visitEval((Eval) n);
        if (n instanceof SettableAssign_c)
            return visitSettableAssign((SettableAssign_c) n);
        // We should be using interfaces (e.g., X10Binary, X10Unary) instead, but
        // (a) there is no X10Unary, and (b) the method name functions are only
        // available on concrete classes anyway.
        if (n instanceof X10Binary_c)
            return visitBinary((X10Binary_c) n);
        if (n instanceof X10Unary_c)
            return visitUnary((X10Unary_c) n);
        if (n instanceof X10Cast_c)
            return visitCast((X10Cast_c) n);
        if (n instanceof X10Instanceof_c)
            return visitInstanceof((X10Instanceof_c) n);
        return n;
    }

    private Expr visitFuture(Future f) throws SemanticException {
        return visitRemoteClosure(f, EVAL_FUTURE, f.place());
    }

    private Expr visitAtExpr(AtExpr e) throws SemanticException {
        return visitRemoteClosure(e, EVAL_AT, e.place());
    }

    Expr getPlace(Position pos, Expr place) throws SemanticException{
    	if (! xts.isImplicitCastValid(place.type(), xts.Place(), context)) {
            	place = synth.makeInstanceCall(pos, place, xts.homeName(),
            			Collections.EMPTY_LIST, 
            			Collections.EMPTY_LIST,
            			xts.Place(),
            			Collections.EMPTY_LIST,
            			xContext());
            }
    	return place;
    }
    private Expr visitRemoteClosure(Closure c, Name implName, Expr place) throws SemanticException {
        Position pos = c.position();
        place = getPlace(pos, place);
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { c.returnType() });
        Position bPos = c.body().position();
        ClosureDef cDef = c.closureDef().position(bPos);
        Expr closure = xnf.Closure(c, bPos)
            .closureDef(cDef)
        	.type(ClosureSynthesizer.closureAnonymousClassDef((X10TypeSystem_c) xts, cDef).asType());
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { place, closure }));
        List<Type> mArgs = new ArrayList<Type>(Arrays.asList(new Type[] {
            xts.Place(), cDef.asType()
        }));
       // List<Type> tArgs = Arrays.asList(new Type[] { fDef.returnType().get() });

        Expr result = synth.makeStaticCall(pos, xts.Runtime(), implName,
        		typeArgs, args, c.type(), xContext());
        return result;
    }

    private Stmt atStmt(Position pos, Stmt body, Expr place) throws SemanticException {
      	place = getPlace(pos, place);
      	Expr placeId = synth.makeFieldAccess(pos, place, Name.make("id"), xContext());
        Closure closure =
        	synth.makeClosure(body.position(), xts.Void(),  synth.toBlock(body), xContext());
        Stmt result = xnf.Eval(pos,
        		synth.makeStaticCall(pos, xts.Runtime(), RUN_AT,
        				Arrays.asList(new Expr[] { placeId, closure }), xts.Void(),
        				xContext()));
        return result;
    }

    private Stmt visitAtStmt(AtStmt a) throws SemanticException {
        Position pos = a.position();
        return atStmt(pos, a.body(), a.place());
    }

    // Begin asyncs
    private Stmt visitAsync(Node old, Async a) throws SemanticException {
        Position pos = a.position();
        if (old instanceof Async && ((Async) old).place() instanceof Here)
            return async(pos, a.body(), a.clocks());
        Stmt specializedAsync = specializeAsync(old, a);
        if (specializedAsync != null)
            return specializedAsync;
        return async(pos, a.body(), a.clocks(), a.place());
    }

    // FIXME: code copied from Emitter
    public boolean hasAnnotation(Node n, QName name) {
        try {
            if (Emitter.annotationNamed(xts, n, name) != null)
                return true;
        } catch (NoClassException e) {
            if (!e.getClassName().equals(name.toString()))
                throw new InternalCompilerError("Something went terribly wrong", e);
        } catch (SemanticException e) {
            throw new InternalCompilerError("Something is terribly wrong", e);
        }
        return false;
    }

    // TODO: add more rules from SPMDcppCodeGenerator
    // TODO: also handle ValRail.apply() and global field accesses
    private boolean isGloballyAvailable(Expr e) {
        if (e instanceof Local)
            return true;
        return false;
    }

    private static final Name XOR = Name.make("xor");
    private static final Name FENCE = Name.make("fence");
    private static final QName IMMEDIATE = QName.make("x10.compiler.Immediate");
    private static final QName REMOTE_OPERATION = QName.make("x10.compiler.RemoteOperation");

    /**
     * Recognize the following pattern:
     * <pre>
     * @Immediate async (p) {
     *     r(i) ^= v;
     * }
     * </pre>
     * where <tt>p: Place</tt>, <tt>r: Rail[T]!p</tt>, <tt>i:Int</tt>, and <tt>v:T</tt>,
     * and compile it into an optimized remote operation.
     * @param a the async statement
     * @return an invocation of the remote operation, or null if no match
     * @throws SemanticException
     * TODO: move into a separate pass!
     */
    private Stmt specializeAsync(Node old, Async a) throws SemanticException {
        if (!hasAnnotation(a, QName.make("x10.compiler.Immediate")))
            return null;
        if (a.clocks().size() != 0)
            return null;
        if (!(old instanceof Async))
            return null;
        Async o = (Async) old;
        Stmt body = o.body();
        if (body instanceof Block) {
            List<Stmt> stmts = ((Block) body).statements();
            if (stmts.size() != 1)
                return null;
            body = stmts.get(0);
        }
        if (!(body instanceof Eval))
            return null;
        Expr e = ((Eval) body).expr();
        if (!(e instanceof SettableAssign_c))
            return null;
        if (((SettableAssign_c) e).operator() != Assign.BIT_XOR_ASSIGN)
            return null;
        List<Expr> is = ((SettableAssign_c) e).index();
        if (is.size() != 1)
            return null;
        Expr i = is.get(0);
        Expr p = a.place();
        Expr r = ((SettableAssign_c) e).array();
        Expr v = ((SettableAssign_c) e).right();
        if (!isGloballyAvailable(r) || !isGloballyAvailable(i) || !isGloballyAvailable(v))
            return null;
        List<Type> ta = ((X10ClassType) X10TypeMixin.baseType(r.type())).typeArguments();
        if (!v.type().isLong() || !xts.isRailOf(r.type(), xts.Long()))
            return null;
        if (!xts.isAtPlace(r, p, xContext()))
            return null;
        ClassType RemoteOperation = (ClassType) xts.typeForName(REMOTE_OPERATION);
        Position pos = a.position();
        List<Expr> args = new ArrayList<Expr>();
        Expr p1 = (Expr) leaveCall(null, p, this);
        args.add(p1);
        args.add((Expr) leaveCall(null, r, this));
        args.add((Expr) leaveCall(null, i, this));
        args.add((Expr) leaveCall(null, v, this));
        Stmt alt = xnf.Eval(pos, synth.makeStaticCall(pos, RemoteOperation, XOR, args, xts.Void(), xContext()));
        Expr cond = xnf.Binary(pos, p1, Binary_c.EQ, visitHere(xnf.Here(pos))).type(xts.Boolean());
        Stmt cns = a.body();
        return xnf.If(pos, cond, cns, alt);
    }

    private Stmt async(Position pos, Stmt body, List<Expr> clocks, Expr place) throws SemanticException {
        if (xts.isImplicitCastValid(place.type(), xts.Object(), context)) {
            place = synth.makeFieldAccess(pos,place, xts.homeName(), xContext());
        }
        if (clocks.size() == 0)
        	return async(pos, body, place);
        Type clockRailType = xts.ValRail(xts.Clock());
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);

        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { place, clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { xts.Place(), clockRailType})),
                             body);
    }


    private Stmt async(Position pos, Stmt body, Expr place) throws SemanticException {
    	List<Expr> l = new ArrayList<Expr>(1);
    	l.add(place);
    	List<Type> t = new ArrayList<Type>(1);
    	t.add(xts.Place());
    	return makeAsyncBody(pos, l, t, body);
    }

    private Stmt async(Position pos, Stmt body, List clocks) throws SemanticException {
        if (clocks.size() == 0)
        	return async(pos, body);
        Type clockRailType = xts.ValRail(xts.Clock());
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { clockRailType})), body);
    }

    private Stmt async(Position pos, Stmt body) throws SemanticException {
        return makeAsyncBody(pos, new LinkedList<Expr>(),
                new LinkedList<Type>(), body);
    }

    private Stmt makeAsyncBody(Position pos, List<Expr> exprs, List<Type> types, Stmt body) throws SemanticException {
        Closure closure = synth.makeClosure(body.position(), xts.Void(),
                synth.toBlock(body), xContext());
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        Stmt result = xnf.Eval(pos,
                synth.makeStaticCall(pos, xts.Runtime(), RUN_ASYNC, exprs,
                        xts.Void(), types, xContext()));
        return result;
    }
    // end Async


    private Expr visitHere(Here h) throws SemanticException {
        Position pos = h.position();
        return call(pos, HERE, xts.Place());
    }

    private Stmt visitNext(Next n) throws SemanticException {
        Position pos = n.position();
        return xnf.Eval(pos, call(pos, NEXT, xts.Void()));
    }

    private Stmt visitAtomic(Atomic a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, LOCK, xts.Void())), a.body());
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, RELEASE, xts.Void())));
        return xnf.Try(pos, tryBlock, new LinkedList(), finallyBlock);
    }

    private Stmt visitAwait(Await a) throws SemanticException {
        Position pos = a.position();
        return xnf.Try(pos,
        		xnf.Block(pos,
                		xnf.Eval(pos, call(pos, LOCK, xts.Void())),
                        xnf.While(pos,
                        		xnf.Unary(pos, a.expr(), Unary.NOT),
                        		xnf.Eval(pos, call(pos, AWAIT, xts.Void())))),
        		Collections.EMPTY_LIST,
        		xnf.Block(pos,
        				xnf.Eval(pos, call(pos, RELEASE, xts.Void()))));
    }

    private Stmt wrap(Position pos, Stmt s) {
        return s.reachable() ? xnf.Block(pos, s, xnf.Break(pos)) : s;
    }

    private Stmt visitWhen(When w) throws SemanticException {
        Position pos = w.position();
        Block body = xnf.Block(pos, xnf.If(pos, w.expr(), wrap(pos, w.stmt())));
        for(int i=0; i<w.stmts().size(); i++) {
            body = body.append(xnf.If(pos, (Expr) w.exprs().get(i), wrap(pos, (Stmt) w.stmts().get(i))));
        }
        body = body.append(xnf.Eval(pos, call(pos, AWAIT, xts.Void())));
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, LOCK, xts.Void())),
                xnf.While(pos, xnf.BooleanLit(pos, true), body));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, RELEASE, xts.Void())));
        return xnf.Try(pos, tryBlock, Collections.EMPTY_LIST, finallyBlock);
    }

    private Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, xts.Runtime(), name,  returnType, xContext());
    }

    private Stmt specializeFinish(Finish f) throws SemanticException {
        if (!hasAnnotation(f, QName.make("x10.compiler.Immediate")))
            return null;
        Position pos = f.position();
        ClassType target = (ClassType) xts.typeForName(REMOTE_OPERATION);
        List<Expr> args = new ArrayList<Expr>();
        return xnf.Block(pos, f.body(), xnf.Eval(pos, synth.makeStaticCall(pos, target, FENCE, args, xts.Void(), xContext())));
    }

    private Stmt visitFinish(Finish f) throws SemanticException {
        Position pos = f.position();
        Name tmp = getTmp();

        Stmt specializedFinish = specializeFinish(f);
        if (specializedFinish != null)
            return specializedFinish;

        // TODO: merge with the call() function
        MethodInstance mi = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), PUSH_EXCEPTION, Collections.singletonList(xts.Throwable()), context));
        LocalDef lDef = xts.localDef(pos, xts.NoFlags(), Types.ref(xts.Throwable()), tmp);
        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                xnf.CanonicalTypeNode(pos, xts.Throwable()), xnf.Id(pos, tmp)).localDef(lDef);
        Expr local = xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(xts.Throwable());
        Expr call = xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, PUSH_EXCEPTION), Collections.EMPTY_LIST,
                Collections.singletonList(local)).methodInstance(mi).type(xts.Void());

        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, START_FINISH, xts.Void())), f.body());
        Catch catchBlock = xnf.Catch(pos, formal, xnf.Block(pos, xnf.Eval(pos, call)));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, STOP_FINISH, xts.Void())));

        return xnf.Try(pos, tryBlock, Collections.singletonList(catchBlock), finallyBlock);
    }

    private Stmt visitForEach(ForEach f) throws SemanticException {
        Position pos = f.position();
        // Have to desugar some newly-created nodes
        Expr here = visitHere(xnf.Here(f.body().position()));
        Stmt body = async(f.body().position(), f.body(), f.clocks(), here);
        X10Formal formal = (X10Formal) f.formal();
        return xnf.ForLoop(pos, formal, f.domain(), body).locals(formal.explode(this));
    }

    private Stmt visitAtEach(AtEach a) throws SemanticException {
        Position pos = a.position();
        Position bpos = a.body().position();
        Name tmp = getTmp();

        Expr domain = a.domain();
        Type dType = domain.type();
        if (((X10TypeSystem_c) xts).isX10Array(dType)) {
            FieldInstance fDist = dType.toClass().fieldNamed(DIST);
            dType = fDist.type();
            domain = xnf.Field(pos, domain, xnf.Id(pos, DIST)).fieldInstance(fDist).type(dType);
        }
        LocalDef lDef = xts.localDef(pos, xts.Final(), Types.ref(dType), tmp);
        LocalDecl local = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, dType), xnf.Id(pos, tmp), domain).localDef(lDef);
        X10Formal formal = (X10Formal) a.formal();
        Type fType = formal.type().type();
        assert (xts.isPoint(fType));
        assert (((X10TypeSystem_c) xts).isDistribution(dType));
        MethodInstance mi = xts.findMethod(dType,
                xts.MethodMatcher(dType, APPLY, Collections.singletonList(fType), context));
        List<Expr> index = new ArrayList<Expr>();
        if (formal.isUnnamed()) {
            ArrayList<Expr> vars = new ArrayList<Expr>();
            ArrayList<Type> varTypes = new ArrayList<Type>();
            for (LocalDef ld : formal.localInstances()) {
                Type t = ld.type().get();
                vars.add(xnf.Local(bpos,
                        nf.Id(bpos, ld.name()))
                        .localInstance(ld.asInstance())
                        .type(t));
                varTypes.add(t);
            }
            MethodInstance mi1 = xts.findMethod(dType,
                    xts.MethodMatcher(dType, APPLY, varTypes, context));
            if (mi1 != null) {
                mi = mi1;
                index = vars;
            } else {
                Type intRail = xts.ValRail(xts.Int());
                MethodInstance cnv = xts.findMethod(fType,
                        xts.MethodMatcher(fType, CONVERT_IMPLICITLY,
                                Collections.singletonList(intRail), context));
                assert (cnv.flags().isStatic());
                index.add(xnf.Call(bpos, xnf.CanonicalTypeNode(bpos, fType),
                        xnf.Id(bpos, CONVERT_IMPLICITLY),
                        xnf.Tuple(bpos, vars).type(intRail)).methodInstance(cnv).type(fType));
            }
        } else {
            index.add(xnf.Local(bpos,
                    xnf.Id(bpos, formal.name().id()))
                    .localInstance(formal.localDef().asInstance())
                    .type(fType));
        }
        Expr place = xnf.Call(bpos,
                xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                xnf.Id(bpos, APPLY),
                index).methodInstance(mi).type(xts.Place());
        Stmt body = async(bpos, a.body(), a.clocks(), place);
        return xnf.Block(pos,
                local,
                xnf.ForLoop(pos, formal,
                        xnf.Local(pos, xnf.Id(pos, tmp))
                        .localInstance(lDef.asInstance())
                        .type(dType),
                        body).locals(formal.explode(this)));
    }

    private Expr visitBinary(X10Binary_c n) throws SemanticException {
        Position pos = n.position();

        Call c = X10Binary_c.desugarBinaryOp(n, this);
        if (c != null) {
            return c;
        }

        return n;
    }

    /**
     * Same as xnf.Assign(...), but also set the appropriate type objects, which the
     * node factory screws up.
     * @throws SemanticException
     */
    private Assign assign(Position pos, Expr e, Assign.Operator asgn, Expr val) throws SemanticException {
        Assign a = (Assign) xnf.Assign(pos, e, asgn, val).type(e.type());
        if (a instanceof FieldAssign) {
            assert (e instanceof Field);
            assert ((Field) e).fieldInstance() != null;
            a = ((FieldAssign) a).fieldInstance(((Field)e).fieldInstance());
        } else if (a instanceof SettableAssign_c) {
            assert (e instanceof X10Call);
            X10Call call = (X10Call) e;
            X10Call_c n = (X10Call_c) xnf.X10Call(pos, call.target(), nf.Id(pos, SET), call.typeArguments(), CollectionUtil.append(Collections.singletonList(val), call.arguments()));
            n = (X10Call_c) n.del().disambiguate(this).typeCheck(this).checkConstants(this);
            MethodInstance smi = n.methodInstance();
//            MethodInstance ami = call.methodInstance();
//            List<Type> aTypes = new ArrayList<Type>(ami.formalTypes());
//            aTypes.add(0, ami.returnType()); // rhs goes before index
//            MethodInstance smi = xts.findMethod(ami.container(),
//                    xts.MethodMatcher(ami.container(), SET, aTypes, context));
            a = ((SettableAssign_c) a).methodInstance(smi);
        }
        return a;
    }

    // ++x -> x+=1 or --x -> x-=1
    private Expr unaryPre(Position pos, X10Unary_c.Operator op, Expr e) throws SemanticException {
        Type ret = e.type();
        Expr one = xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, ret),
                (Expr) xnf.IntLit(pos, IntLit.INT, 1).typeCheck(this), X10Cast.ConversionType.PRIMITIVE).type(ret);
        Assign.Operator asgn = (op == X10Unary_c.PRE_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        Expr a = assign(pos, e, asgn, one);
        if (e instanceof X10Call)
            a = visitSettableAssign((SettableAssign_c) a);
        return a;
    }

    // x++ -> ((t:Int)=>t-1)(x+=1) or x-- -> ((t:Int)=>t+1)(x-=1)
    private Expr unaryPost(Position pos, X10Unary_c.Operator op, Expr e) throws SemanticException {
        Type ret = e.type();
        CanonicalTypeNode retTN = xnf.CanonicalTypeNode(pos, ret);
        Expr one = xnf.X10Cast(pos, retTN,
                (Expr) xnf.IntLit(pos, IntLit.INT, 1).typeCheck(this), X10Cast.ConversionType.PRIMITIVE).type(ret);
        Assign.Operator asgn = (op == X10Unary_c.POST_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        X10Binary_c.Operator bin = (op == X10Unary_c.POST_INC) ? X10Binary_c.SUB : X10Binary_c.ADD;
        Name t = Name.make("t");
        LocalDef fDef = xts.localDef(pos, xts.NoFlags(), Types.ref(ret), t);
        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                retTN, xnf.Id(pos, t)).localDef(fDef);
        List<Formal> parms = Arrays.asList(new Formal[] { formal });
        Expr tLocal = xnf.Local(pos, xnf.Id(pos, t)).localInstance(fDef.asInstance()).type(ret);
        X10Cast cast = (X10Cast) xnf.X10Cast(pos, retTN, xnf.Binary(pos, tLocal, bin, one).type(ret), X10Cast.ConversionType.PRIMITIVE).type(ret);
        Block block = xnf.Block(pos, xnf.Return(pos, cast));
        Closure c = synth.makeClosure(pos, e.type(), parms, block, (X10Context) context);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        Expr incr = assign(pos, e, asgn, one);
        if (e instanceof X10Call)
            incr = visitSettableAssign((SettableAssign_c) incr);
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { incr }));
        return xnf.ClosureCall(pos, c,  args).closureInstance(ci).type(ret);
    }

    private Expr visitUnary(X10Unary_c n) throws SemanticException {
        Position pos = n.position();

        Expr left = n.expr();
        Type l = left.type();
        X10Unary_c.Operator op = n.operator();

        if (op == X10Unary_c.PRE_DEC || op == X10Unary_c.PRE_INC) {
            return unaryPre(pos, op, n.expr());
        }
        if (op == X10Unary_c.POST_DEC || op == X10Unary_c.POST_INC) {
            return unaryPost(pos, op, n.expr());
        }

        Call c = X10Unary_c.desugarUnaryOp(n, this);
        if (c != null) {
            return c;
        }

        return n;
    }

    private Stmt visitEval(Eval n) throws SemanticException {
        Position pos = n.position();
        if (n.expr() instanceof X10Unary_c) {
            X10Unary_c e = (X10Unary_c) n.expr();
            Position ePos = e.position();
            if (e.operator() == X10Unary_c.POST_DEC)
                return xnf.Eval(pos,
                        visitUnary((X10Unary_c) xnf.Unary(ePos, X10Unary_c.PRE_DEC, e.expr())));
            if (e.operator() == X10Unary_c.POST_INC)
                return xnf.Eval(pos,
                        visitUnary((X10Unary_c) xnf.Unary(ePos, X10Unary_c.PRE_INC, e.expr())));
        }
        return n;
    }

    // a(i)=v -> a.set(v, i) or a(i)op=v -> ((x:A,y:I,z:T)=>x.set(x.apply(y) op z,y))(a,i,v)
    private Expr visitSettableAssign(SettableAssign_c n) throws SemanticException {
        Position pos = n.position();
        MethodInstance mi = n.methodInstance();
        List<Expr> args = new ArrayList<Expr>(n.index());
        if (n.operator() == Assign.ASSIGN) {
            // FIXME: this changes the order of evaluation, (a,i,v) -> (a,v,i)!
            args.add(0, n.right());
            return xnf.Call(pos, n.array(), xnf.Id(pos, mi.name()),
                    args).methodInstance(mi).type(mi.returnType());
        }
        X10Binary_c.Operator op = SettableAssign_c.binaryOp(n.operator());
        X10Call left = (X10Call) n.left(xnf, this);
        MethodInstance ami = left.methodInstance();
        List<Formal> parms = new ArrayList<Formal>();
        Name xn = Name.make("x");
        LocalDef xDef = xts.localDef(pos, xts.Final(), Types.ref(mi.container()), xn);
        Formal x = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, mi.container()), xnf.Id(pos, xn)).localDef(xDef);
        parms.add(x);
        List<Expr> idx1 = new ArrayList<Expr>();
        int i = 0;
        for (Type t : ami.formalTypes()) {
            Name yn = Name.make("y"+i);
            LocalDef yDef = xts.localDef(pos, xts.Final(), Types.ref(t), yn);
            Formal y = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                    xnf.CanonicalTypeNode(pos, t), xnf.Id(pos, yn)).localDef(yDef);
            parms.add(y);
            idx1.add(xnf.Local(pos, xnf.Id(pos, yn)).localInstance(yDef.asInstance()).type(t));
            i++;
        }
        Name zn = Name.make("z");
        Type T = mi.formalTypes().get(0);
        assert (xts.typeEquals(T, ami.returnType(), context));
        LocalDef zDef = xts.localDef(pos, xts.Final(), Types.ref(T), zn);
        Formal z = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, T), xnf.Id(pos, zn)).localDef(zDef);
        parms.add(z);
        Expr val = visitBinary((X10Binary_c) xnf.Binary(pos,
                xnf.Call(pos,
                        xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(mi.container()),
                        xnf.Id(pos, ami.name()), idx1).methodInstance(ami).type(T),
                op, xnf.Local(pos, xnf.Id(pos, zn)).localInstance(zDef.asInstance()).type(T)).type(T));
        List<Expr> args1 = new ArrayList<Expr>(idx1);
        args1.add(0, val);
        Type ret = mi.returnType();
        Expr res = xnf.Call(pos,
                xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(mi.container()),
                xnf.Id(pos, mi.name()), args1).methodInstance(mi).type(ret);
        // Have to create the appropriate node in case someone defines a set():void
        Block block = ret.isVoid() ?
                xnf.Block(pos, xnf.Eval(pos, res), xnf.Return(pos, xnf.Call(pos,
                        xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(mi.container()),
                        xnf.Id(pos, ami.name()), idx1).methodInstance(ami).type(T))) :
                xnf.Block(pos, xnf.Return(pos, res));
        Closure c = synth.makeClosure(pos, T, parms, block, (X10Context) context);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        args.add(0, n.array());
        args.add(n.right());
        return xnf.ClosureCall(pos, c, args).closureInstance(ci).type(ret);
    }

    /**
     * Concatenates the given list of clauses with &&, creating a conjunction.
     * Any occurrence of "self" in the list of clauses is replaced by self.
     */
    private Expr conjunction(Position pos, List<Expr> clauses, Expr self) {
        assert clauses.size() > 0;
        Substitution<Expr> subst = new Substitution<Expr>(Expr.class, Collections.singletonList(self)) {
            protected Expr subst(Expr n) {
                if (n instanceof X10Special_c && ((X10Special_c) n).kind() == X10Special_c.SELF)
                    return by.get(0);
                return n;
            }
        };
        Expr left = null;
        for (Expr clause : clauses) {
            Expr right = (Expr) clause.visit(subst);
            if (left == null)
                left = right;
            else
                left = xnf.Binary(pos, left, X10Binary_c.COND_AND, right).type(xts.Boolean());
        }
        return left;
    }

    private DepParameterExpr getClause(TypeNode tn) {
        if (tn instanceof X10CanonicalTypeNode) {
            X10CanonicalTypeNode ctn = (X10CanonicalTypeNode) tn;
            return ctn.constraintExpr();
        } else {
            assert false : "Unknown type node type: "+tn.getClass();
        }
        return null;
    }

    private TypeNode stripClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            X10CanonicalTypeNode ctn = (X10CanonicalTypeNode) tn;
            return xnf.CanonicalTypeNode(tn.position(), X10TypeMixin.baseType(t));
        } else {
            assert false : "Unknown type node type: "+tn.getClass();
        }
        return tn;
    }

    // e as T{c} -> ((x:T):T{c}=>{if (x!=null&&!c[self/x]) throwCCE(); return x;})(e as T)
    private Expr visitCast(X10Cast_c n) throws SemanticException {
        Position pos = n.position();
        Expr e = n.expr();
        TypeNode tn = n.castType();
        Type ot = tn.type();
        DepParameterExpr depClause = getClause(tn);
        tn = stripClause(tn);
        if (depClause == null || Configuration.NO_CHECKS)
            return n;
        Name xn = getTmp();
        Type t = tn.type(); // the base type of the cast
        LocalDef xDef = xts.localDef(pos, xts.Final(), Types.ref(t), xn);
        Formal x = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, t), xnf.Id(pos, xn)).localDef(xDef);
        Expr xl = xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(t);
        List<Expr> condition = depClause.condition();
        Expr cond = xnf.Unary(pos, conjunction(depClause.position(), condition, xl), Unary.NOT).type(xts.Boolean());
        if (xts.isSubtype(t, xts.Object(), context)) {
            Expr nonnull = xnf.Binary(pos, xl, X10Binary_c.NE, xnf.NullLit(pos).type(xts.Null())).type(xts.Boolean());
            cond = xnf.Binary(pos, nonnull, X10Binary_c.COND_AND, cond);
        }
        Type ccet = xts.ClassCastException();
        CanonicalTypeNode CCE = xnf.CanonicalTypeNode(pos, ccet);
        Expr msg = xnf.StringLit(pos, ot.toString()).type(xts.String());
        X10ConstructorInstance ni = xts.findConstructor(ccet, xts.ConstructorMatcher(ccet, Collections.singletonList(xts.String()), context));
        Expr newCCE = xnf.New(pos, CCE, Collections.singletonList(msg)).constructorInstance(ni).type(ccet);
        Stmt throwCCE = xnf.Throw(pos, newCCE);
        Stmt check = xnf.If(pos, cond, throwCCE);
        Block body = xnf.Block(pos, check, xnf.Return(pos, xl));
        Closure c = synth.makeClosure(pos, ot, Collections.singletonList(x), body, (X10Context) context);
        Expr cast = xnf.X10Cast(pos, tn, e, X10Cast.ConversionType.CHECKED).type(t);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        return xnf.ClosureCall(pos, c, Collections.singletonList(cast)).closureInstance(ci).type(ot);
    }

    // e instanceof T{c} -> ((x:F)=>x instanceof T && c[self/x as T])(e)
    private Expr visitInstanceof(X10Instanceof_c n) throws SemanticException {
        Position pos = n.position();
        Expr e = n.expr();
        TypeNode tn = n.compareType();
        DepParameterExpr depClause = getClause(tn);
        tn = stripClause(tn);
        if (depClause == null)
            return n;
        Name xn = getTmp();
        Type et = e.type();
        LocalDef xDef = xts.localDef(pos, xts.Final(), Types.ref(et), xn);
        Formal x = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, et), xnf.Id(pos, xn)).localDef(xDef);
        Expr xl = xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(et);
        Expr iof = xnf.Instanceof(pos, xl, tn).type(xts.Boolean());
        Expr cast = xnf.X10Cast(pos, tn, xl, X10Cast.ConversionType.CHECKED).type(tn.type());
        List<Expr> condition = depClause.condition();
        Expr cond = conjunction(depClause.position(), condition, cast);
        Expr rval = xnf.Binary(pos, iof, X10Binary_c.COND_AND, cond).type(xts.Boolean());
        Block body = xnf.Block(pos, xnf.Return(pos, rval));
        Closure c = synth.makeClosure(pos, xts.Boolean(), Collections.singletonList(x), body, (X10Context) context);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        return xnf.ClosureCall(pos, c, Collections.singletonList(e)).closureInstance(ci).type(xts.Boolean());
    }

    public static class Substitution<T extends Node> extends ErrorHandlingVisitor {
        protected final List<T> by;
        private final Class<T> cz;
        public Substitution(Class<T> cz, List<T> by) {
            super(null, null, null);
            this.cz = cz;
            this.by = by;
        }
        protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            if (cz.isInstance(n))
                return subst((T)n);
            return n;
        }
        protected T subst(T n) {
            return n;
        }
    }
}
