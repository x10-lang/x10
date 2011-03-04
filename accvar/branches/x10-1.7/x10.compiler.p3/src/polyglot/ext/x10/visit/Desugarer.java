/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Catch;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.AtExpr;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.AtStmt;
import polyglot.ext.x10.ast.Await;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.SettableAssign_c;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.When;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10New_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.constraint.XRoot;

/**
 * Visitor to desugar the AST before code gen.
 */
public class Desugarer extends ContextVisitor {
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }

    private static int count;

    private static Name getTmp() {
        return Name.make("__desugarer__var__" + (count++) + "__");
    }

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
    private static final Name CONVERT = Name.make("$convert");
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
        return n;
    }

    private Expr visitFuture(Future f) throws SemanticException {
        return visitRemoteClosure(f, EVAL_FUTURE, f.place(), true);
    }

    private Expr visitAtExpr(AtExpr e) throws SemanticException {
        return visitRemoteClosure(e, EVAL_AT, e.place(), false);
    }

    private Expr visitRemoteClosure(Closure c, Name implName, Expr place, boolean named) throws SemanticException {
        Position pos = c.position();
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { c.returnType() });
        ClosureDef fDef = c.closureDef();
        // TODO: factor out common functionality with the closure() method
        ClosureDef cDef = xts.closureDef(c.body().position(), fDef.typeContainer(),
                fDef.methodContainer(), fDef.returnType(),
                fDef.typeParameters(), fDef.formalTypes(), fDef.thisVar(),
                fDef.formalNames(), fDef.guard(), fDef.typeGuard(), fDef.throwTypes());
        Closure closure = (Closure) xnf.Closure(c.body().position(), c.typeParameters(),
                c.formals(), c.guard(), c.returnType(),
                c.throwTypes(), c.body()).closureDef(cDef).type(xts.closureAnonymousClassDef(cDef).asType());
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { place, closure }));
        List<Type> mArgs = new ArrayList<Type>(Arrays.asList(new Type[] {
            xts.Place(), closure.closureDef().asType()
        }));
        if (named) {
            args.add(xnf.StringLit(pos, pos.nameAndLineString()).type(xts.String()));
            mArgs.add(xts.String());
        }
        List<Type> tArgs = Arrays.asList(new Type[] { fDef.returnType().get() });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), implName, mArgs, context));
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, implName), typeArgs, args).methodInstance(implMI).type(c.type());
    }

    private Stmt atStmt(Position pos, Stmt body, Expr place) throws SemanticException {
        Block block = body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
        Closure closure = closure(body.position(), xts.Void(), Collections.EMPTY_LIST, block);
        List<Expr> args = Arrays.asList(new Expr[] { place, closure });
        List<Type> mArgs = Arrays.asList(new Type[] { xts.Place(), closure.closureDef().asType() });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), RUN_AT, mArgs, context));
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_AT), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Stmt visitAtStmt(AtStmt a) throws SemanticException {
        Position pos = a.position();
        return atStmt(pos, a.body(), a.place());
    }

    private Closure closure(Position pos, Type retType, List<Formal> parms, Block body) {
        List<Ref<? extends Type>> fTypes = new ArrayList<Ref<? extends Type>>();
        List<LocalDef> fNames = new ArrayList<LocalDef>();
        for (Formal f : parms) {
            fTypes.add(Types.ref(f.type().type()));
            fNames.add(f.localDef());
        }
        ClosureDef cDef = xts.closureDef(pos, Types.ref(context.currentClass()),
                Types.ref(context.currentCode().asInstance()),
                Types.ref(retType), Collections.EMPTY_LIST,
                fTypes, (XRoot) null, fNames, null, null, Collections.EMPTY_LIST);
        Closure closure = (Closure) xnf.Closure(pos, Collections.EMPTY_LIST,
                parms, null, xnf.CanonicalTypeNode(pos, retType),
                Collections.EMPTY_LIST, body).closureDef(cDef).type(xts.closureAnonymousClassDef(cDef).asType());
        return closure;
    }

    private Stmt async(Position pos, Stmt body, List clocks, Expr place, String prefix) throws SemanticException {
        if (clocks.size() == 0) return async(pos, body, place, prefix);
        Type clockRailType = xts.ValRail(xts.Clock());
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
        Block block = body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
        Closure closure = closure(body.position(), xts.Void(), Collections.EMPTY_LIST, block);
        StringLit pString = (StringLit) xnf.StringLit(pos, prefix + pos.nameAndLineString()).type(xts.String());
        List<Expr> args = Arrays.asList(new Expr[] { place, clockRail, closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] {
            xts.Place(), clockRailType, closure.closureDef().asType(), xts.String()
        });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), RUN_ASYNC, mArgs, context));
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_ASYNC), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Stmt async(Position pos, Stmt body, Expr place, String prefix) throws SemanticException {
        Block block = body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
        Closure closure = closure(body.position(), xts.Void(), Collections.EMPTY_LIST, block);
        StringLit pString = (StringLit) xnf.StringLit(pos, prefix + pos.nameAndLineString()).type(xts.String());
        List<Expr> args = Arrays.asList(new Expr[] { place, closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] {
            xts.Place(), closure.closureDef().asType(), xts.String()
        });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), RUN_ASYNC, mArgs, context));
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_ASYNC), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Stmt async(Position pos, Stmt body, List clocks, String prefix) throws SemanticException {
        if (clocks.size() == 0) return async(pos, body, prefix);
        Type clockRailType = xts.ValRail(xts.Clock());
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
        Block block = body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
        Closure closure = closure(body.position(), xts.Void(), Collections.EMPTY_LIST, block);
        StringLit pString = (StringLit) xnf.StringLit(pos, prefix + pos.nameAndLineString()).type(xts.String());
        List<Expr> args = Arrays.asList(new Expr[] { clockRail, closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] {
            clockRailType, closure.closureDef().asType(), xts.String()
        });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), RUN_ASYNC, mArgs, context));
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_ASYNC), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Stmt async(Position pos, Stmt body, String prefix) throws SemanticException {
        Block block = body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
        Closure closure = closure(body.position(), xts.Void(), Collections.EMPTY_LIST, block);
        StringLit pString = (StringLit) xnf.StringLit(pos, prefix + pos.nameAndLineString()).type(xts.String());
        List<Expr> args = Arrays.asList(new Expr[] { closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] {
            closure.closureDef().asType(), xts.String()
        });
        // TODO: merge with the call() function
        MethodInstance implMI = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), RUN_ASYNC, mArgs, context));
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_ASYNC), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Stmt visitAsync(Node old, Async a) throws SemanticException {
        Position pos = a.position();
        if (old instanceof Async && ((Async) old).place() instanceof Here)
            return async(pos, a.body(), a.clocks(), "async-");
        return async(pos, a.body(), a.clocks(), a.place(), "async-");
    }

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
        return xnf.Try(pos, tryBlock, Collections.EMPTY_LIST, finallyBlock);
    }
    
    private Stmt visitAwait(Await a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, LOCK, xts.Void())), 
                xnf.While(pos, xnf.Unary(pos, a.expr(), Unary.NOT), xnf.Eval(pos, call(pos, AWAIT, xts.Void()))));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, RELEASE, xts.Void())));
        return xnf.Try(pos, tryBlock, Collections.EMPTY_LIST, finallyBlock);
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
    
    private Expr call(Position pos, Name name, Type ret) throws SemanticException {
        MethodInstance mi = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), name, Collections.EMPTY_LIST, context));
        assert (xts.typeEquals(ret, mi.returnType(), context));
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, name), Collections.EMPTY_LIST,
                Collections.EMPTY_LIST).methodInstance(mi).type(mi.returnType());
    }

    private Stmt visitFinish(Finish f) throws SemanticException {
        Position pos = f.position();
        Name tmp = getTmp();

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
        Stmt body = async(f.body().position(), f.body(), f.clocks(), here, "foreach-");
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
        Expr index = xnf.Local(bpos,
                xnf.Id(bpos, formal.name().id())).localInstance(formal.localDef().asInstance()).type(fType);
        if (formal.isUnnamed()) {
            ArrayList<Expr> vars = new ArrayList<Expr>();
            for (LocalDef ld : formal.localInstances()) {
                vars.add(xnf.Local(bpos,
                        nf.Id(bpos, ld.name())).localInstance(ld.asInstance()).type(ld.type().get()));
            }
            Type intRail = xts.ValRail(xts.Int());
            MethodInstance cnv = xts.findMethod(fType,
                    xts.MethodMatcher(fType, CONVERT, Collections.singletonList(intRail), context));
            assert (cnv.flags().isStatic());
            index =
                xnf.Call(bpos, xnf.CanonicalTypeNode(bpos, fType), xnf.Id(bpos, CONVERT),
                        xnf.Tuple(bpos, vars).type(intRail)).methodInstance(cnv).type(fType);
        }
        Expr place = xnf.Call(bpos,
                xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                xnf.Id(bpos, APPLY),
                index).methodInstance(mi).type(xts.Place());
        Stmt body = async(bpos, a.body(), a.clocks(), place, "ateach-");
        return xnf.Block(pos,
                local,
                xnf.ForLoop(pos, formal,
                        xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                        body).locals(formal.explode(this)));
    }

    private Expr visitBinary(X10Binary_c n) throws SemanticException {
        Position pos = n.position();

        Expr left = n.left();
        Type l = left.type();
        Expr right = n.right();
        Type r = right.type();
        X10Binary_c.Operator op = n.operator();

        if (op == X10Binary_c.EQ || op == X10Binary_c.NE) { // TODO
            return n;
        }
        if (l.isNumeric() && r.isNumeric()) { // TODO: get rid of this special case by defining native operators
            return n;
        }
        if (l.isBoolean() && r.isBoolean()) { // TODO: get rid of this special case by defining native operators
            return n;
        }
        if (op == X10Binary_c.ADD && (l.isSubtype(xts.String(), context) || r.isSubtype(xts.String(), context))) { // TODO: get rid of this special case by defining native operators
            return n;
        }

        boolean inv = n.invert();
        Name methodName = inv ? X10Binary_c.invBinaryMethodName(op) : X10Binary_c.binaryMethodName(op);
        assert (methodName != null) : ("No method to implement at " + pos);
        Expr receiver = inv ? right : left;
        Expr arg = inv ? left : right;
        List<Type> types = Arrays.asList(new Type[] { arg.type() });
        MethodInstance mi = xts.findMethod(receiver.type(),
                xts.MethodMatcher(receiver.type(), methodName, types, context));
        return xnf.Call(pos, receiver, xnf.Id(pos, methodName),
                arg).methodInstance(mi).type(mi.returnType());
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
        Closure c = closure(pos, e.type(), parms, block);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        Expr incr = assign(pos, e, asgn, one);
        if (e instanceof X10Call)
            incr = visitSettableAssign((SettableAssign_c) incr);
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { incr }));
        return xnf.ClosureCall(pos, c, Collections.EMPTY_LIST, args).closureInstance(ci).type(ret);
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
        if (l.isNumeric()) { // TODO: get rid of this special case by defining native operators
            return n;
        }
        if (l.isBoolean()) { // TODO: get rid of this special case by defining native operators
            return n;
        }

        Name methodName = X10Unary_c.unaryMethodName(op);
        assert (methodName != null) : ("No method to implement at " + pos);
        Expr receiver = left;
        List<Type> types = Arrays.asList(new Type[] { });
        MethodInstance mi = xts.findMethod(receiver.type(),
                xts.MethodMatcher(receiver.type(), methodName, types, context));
        return xnf.Call(pos, receiver, xnf.Id(pos, methodName)).methodInstance(mi).type(mi.returnType());
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
        X10Call left = (X10Call) n.left(xnf);
        MethodInstance ami = left.methodInstance();
if (ami == null)
    System.out.print("");
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
        Closure c = closure(pos, T, parms, block);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        args.add(0, n.array());
        args.add(n.right());
        return xnf.ClosureCall(pos, c, Collections.EMPTY_LIST, args).closureInstance(ci).type(ret);
    }
}
