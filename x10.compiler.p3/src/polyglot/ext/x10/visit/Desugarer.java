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

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtExpr;
import polyglot.ext.x10.ast.AtStmt;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.Future;
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
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    private static QName CLOCK_CLASSNAME = QName.make("x10.lang.Clock");
    private static QName RUNTIME_CLASSNAME = QName.make("x10.runtime.Runtime");
    private static final String EVAL_AT = "evalAt";
    private static final String EVAL_FUTURE = "evalFuture";
    private static final String RUN_ASYNC = "runAsync";

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
        return n;
    }

    private Node visitFuture(Future f) throws SemanticException {
        return visitRemoteClosure(f, EVAL_FUTURE, f.place(), true);
    }

    private Node visitAtExpr(AtExpr e) throws SemanticException {
        return visitRemoteClosure(e, EVAL_AT, e.place(), false);
    }

    private Node visitRemoteClosure(Closure c, String impl, Expr place, boolean named) throws SemanticException {
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        Position pos = c.position();
        Type runtime = (Type)xts.forName(RUNTIME_CLASSNAME);
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { c.returnType() });
        ClosureDef fDef = c.closureDef();
        ClosureDef cDef = xts.closureDef(c.body().position(), fDef.typeContainer(),
                                         fDef.methodContainer(), fDef.returnType(),
                                         fDef.typeParameters(), fDef.formalTypes(),
                                         fDef.formalNames(), fDef.guard(), fDef.throwTypes());
        Closure closure = ((Closure_c) xnf.Closure(c.body().position(), c.typeParameters(),
                                                   c.formals(), c.guard(), c.returnType(),
                                                   c.throwTypes(), c.body())).closureDef(cDef);
        List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { place, closure }));
        List<Type> mArgs = new ArrayList<Type>(Arrays.asList(new Type[] { xts.Place(), cDef.asType() }));
        if (named) {
            args.add(xnf.StringLit(pos, pos.nameAndLineString()));
            mArgs.add(xts.String());
        }
        List<Type> tArgs = Arrays.asList(new Type[] { fDef.returnType().get() });
        Name implName = Name.make(impl);
        MethodInstance implMI = xts.findMethod(runtime, xts.MethodMatcher(runtime, implName, mArgs),
                                               context.currentClassDef());
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, runtime),
                           xnf.Id(pos, implName), typeArgs, args).methodInstance(implMI).type(c.type());
    }

    private Node visitAsync(Async a) throws SemanticException {
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        Position pos = a.position();
        Type runtime = (Type)xts.forName(RUNTIME_CLASSNAME);
        ClosureDef cDef = xts.closureDef(a.body().position(), Types.ref(context.currentClass()),
                                         Types.ref(context.currentCode().asInstance()),
                                         Types.ref(xts.Void()), Collections.EMPTY_LIST,
                                         Collections.EMPTY_LIST, Collections.EMPTY_LIST, null,
                                         Collections.EMPTY_LIST);
        Type clockRail = xts.ValRail((Type)xts.forName(CLOCK_CLASSNAME));
        Tuple clocks = (Tuple) xnf.Tuple(pos, a.clocks()).type(clockRail);
        Closure closure = ((Closure_c) xnf.Closure(a.body().position(), Collections.EMPTY_LIST,
                                                   Collections.EMPTY_LIST, null,
                                                   xnf.CanonicalTypeNode(pos, xts.Void()),
                                                   Collections.EMPTY_LIST,
                                                   xnf.Block(a.body().position(), a.body()))).closureDef(cDef);
        StringLit pString = xnf.StringLit(pos, pos.nameAndLineString());
        List<Expr> args = Arrays.asList(new Expr[] { a.place(), clocks, closure, pString });
        List<Type> mArgs = Arrays.asList(new Type[] { xts.Place(), clockRail,
                                                      cDef.asType(), xts.String() });
        Name implName = Name.make(RUN_ASYNC);
        MethodInstance implMI = xts.findMethod(runtime, xts.MethodMatcher(runtime, implName, mArgs),
                                               context.currentClassDef());
        return xnf.Eval(pos,
                        xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, runtime),
                                    xnf.Id(pos, implName), Collections.EMPTY_LIST,
                                    args).methodInstance(implMI).type(xts.Void()));
    }
}
