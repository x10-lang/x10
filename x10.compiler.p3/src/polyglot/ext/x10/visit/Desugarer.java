/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.Arrays;
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

    private static QName RUNTIME_CLASSNAME = QName.make("x10.runtime.Runtime");

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Future)
            return visitFuture((Future) n);
        if (n instanceof Async)
            return visitAsync((Async) n);
        if (n instanceof AtStmt)
            return visitAtStmt((AtStmt) n);
        if (n instanceof AtExpr)
            return visitAtExpr((AtExpr) n);
        return n;
    }

    private Node visitFuture(Future f) throws SemanticException {
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        Position pos = f.position();
        Type runtime = (Type)xts.forName(RUNTIME_CLASSNAME);
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { f.returnType() });
        ClosureDef fDef = f.closureDef();
        ClosureDef cDef = xts.closureDef(f.body().position(), fDef.typeContainer(),
                                         fDef.methodContainer(), fDef.returnType(),
                                         fDef.typeParameters(), fDef.formalTypes(),
                                         fDef.formalNames(), fDef.guard(), fDef.throwTypes());
        Closure closure = ((Closure_c) xnf.Closure(f.body().position(), f.typeParameters(),
                                                   f.formals(), f.guard(), f.returnType(),
                                                   f.throwTypes(), f.body())).closureDef(cDef);
        StringLit pString = nf.StringLit(pos, pos.nameAndLineString());
        List<Expr> args = Arrays.asList(new Expr[] { f.place(), closure, pString });
        Name evalFuture = Name.make("evalFuture");
        List<Type> mArgs = Arrays.asList(new Type[] { xts.Place(), cDef.asType(), xts.String() });
        List<Type> tArgs = Arrays.asList(new Type[] { fDef.returnType().get() });
        MethodInstance eFmi = xts.findMethod(runtime, xts.MethodMatcher(runtime, evalFuture, mArgs), context.currentClassDef());
        return xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, runtime),
                           nf.Id(pos, evalFuture), typeArgs, args).methodInstance(eFmi).type(f.type());
    }

    private Node visitAsync(Async a) throws SemanticException {
        /** To be filled in. */
        return a;
    }

    private Node visitAtStmt(AtStmt s) throws SemanticException {
        /** To be filled in. */
        return s;
    }

    private Node visitAtExpr(AtExpr e) throws SemanticException {
        /** To be filled in. */
        return e;
    }
}
