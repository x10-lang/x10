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

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtExpr;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.AtStmt;
import polyglot.ext.x10.ast.Await;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

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

    private static final Name EVAL_AT = Name.make("evalAt");
    private static final Name EVAL_FUTURE = Name.make("evalFuture");
    private static final Name RUN_ASYNC = Name.make("runAsync");
    private static final Name HERE = Name.make("here");
    private static final Name NEXT = Name.make("next");
    private static final Name LOCK = Name.make("lock");
    private static final Name AWAIT = Name.make("await");
    private static final Name RELEASE = Name.make("release");

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Future)
            return visitFuture((Future) n);
        if (n instanceof Async)
            return visitAsync((Async) n);
        if (n instanceof AtStmt) {
            assert (false) : ("At statements are deprecated");
            return n;
        }
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
        return n;
    }

    private Node visitFuture(Future f) throws SemanticException {
        return visitRemoteClosure(f, EVAL_FUTURE, f.place(), true);
    }

    private Node visitAtExpr(AtExpr e) throws SemanticException {
        return visitRemoteClosure(e, EVAL_AT, e.place(), false);
    }

    private Node visitRemoteClosure(Closure c, Name implName, Expr place, boolean named) throws SemanticException {
        Position pos = c.position();
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { c.returnType() });
        ClosureDef fDef = c.closureDef();
        ClosureDef cDef = xts.closureDef(c.body().position(), fDef.typeContainer(),
                fDef.methodContainer(), fDef.returnType(),
                fDef.typeParameters(), fDef.formalTypes(),
                fDef.formalNames(), fDef.guard(), fDef.throwTypes());
        Closure closure = (Closure_c) ((Closure_c) xnf.Closure(c.body().position(), c.typeParameters(),
                c.formals(), c.guard(), c.returnType(),
                c.throwTypes(), c.body())).closureDef(cDef).type(xts.closureAnonymousClassDef(cDef).asType());
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { place, closure }));
        List<Type> mArgs = new ArrayList<Type>(Arrays.asList(new Type[] { xts.Place(), cDef.asType() }));
        if (named) {
            args.add(xnf.StringLit(pos, pos.nameAndLineString()));
            mArgs.add(xts.String());
        }
        List<Type> tArgs = Arrays.asList(new Type[] { fDef.returnType().get() });
        MethodInstance implMI = xts.findMethod(xts.Runtime(), xts.MethodMatcher(xts.Runtime(), implName, mArgs),
                context.currentClassDef());
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, implName), typeArgs, args).methodInstance(implMI).type(c.type());
    }

    private Node visitAsync(Async a) throws SemanticException {
        Position pos = a.position();
        ClosureDef cDef = xts.closureDef(a.body().position(), Types.ref(context.currentClass()),
                Types.ref(context.currentCode().asInstance()),
                Types.ref(xts.Void()), Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, Collections.EMPTY_LIST, null,
                Collections.EMPTY_LIST);
        Type clockRail = xts.ValRail(xts.Clock());
        Tuple clocks = (Tuple) xnf.Tuple(pos, a.clocks()).type(clockRail);
        Closure closure = (Closure_c) ((Closure_c) xnf.Closure(a.body().position(), Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, null, xnf.CanonicalTypeNode(pos, xts.Void()),
                Collections.EMPTY_LIST, xnf.Block(a.body().position(), a.body()))).closureDef(cDef).type(xts.closureAnonymousClassDef(cDef).asType());
        StringLit pString = xnf.StringLit(pos, pos.nameAndLineString());
        List<Expr> args = Arrays.asList(new Expr[] { a.place(), clocks, closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] { xts.Place(), clockRail,
                cDef.asType(), xts.String() });
        MethodInstance implMI = xts.findMethod(xts.Runtime(), xts.MethodMatcher(xts.Runtime(), RUN_ASYNC, mArgs),
                context.currentClassDef());
        return xnf.Eval(pos, xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, RUN_ASYNC), Collections.EMPTY_LIST,
                args).methodInstance(implMI).type(xts.Void()));
    }

    private Node visitHere(Here h) throws SemanticException {
        Position pos = h.position();
        return call(pos, HERE, xts.Place());
    }

    private Node visitNext(Next n) throws SemanticException {
        Position pos = n.position();
        return xnf.Eval(pos, call(pos, NEXT, xts.Void()));
    }

    private Node visitAtomic(Atomic a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, LOCK, xts.Void())), a.body());
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, RELEASE, xts.Void())));
        return xnf.Try(pos, tryBlock, Collections.EMPTY_LIST, finallyBlock);
    }
    
    private Node visitAwait(Await a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, LOCK, xts.Void())), 
                xnf.While(pos, xnf.Unary(pos, a.expr(), Unary.NOT), xnf.Eval(pos, call(pos, AWAIT, xts.Void()))));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, RELEASE, xts.Void())));
        return xnf.Try(pos, tryBlock, Collections.EMPTY_LIST, finallyBlock);
    }
    
    private Expr call(Position pos, Name name, Type t) throws SemanticException {
        MethodInstance mi = xts.findMethod(xts.Runtime(), xts.MethodMatcher(xts.Runtime(),
                name, Collections.EMPTY_LIST), context.currentClassDef());
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, name), Collections.EMPTY_LIST,
                Collections.EMPTY_LIST).methodInstance(mi).type(t);
    }
}
