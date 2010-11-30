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
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast;
import x10.ast.X10Instanceof;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.types.X10ConstructorInstance;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.util.Synthesizer;

/**
 * Visitor to desugar the AST before code generation.
 * 
 * NOTE: all the nodes created in the Desugarer must have the appropriate type information.
 * The NodeFactory methods do not fill in the type information.  Use the helper methods available
 * in the Desugarer to create expressions, or see how the type information is filled in for other
 * types of nodes elsewhere in the Desugarer.  TODO: factor out the helper methods into the
 * {@link Synthesizer}.
 */
public class Desugarer extends ContextVisitor {


    private final TypeSystem xts;
    private final NodeFactory xnf;
    private final Synthesizer synth;
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        xnf = nf;
        synth = new Synthesizer(xnf, xts);
    }

    private static int count;

    private static Name getTmp() {
        return Name.make("__desugarer__var__" + (count++) + "__");
    }

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
        if (n instanceof ParExpr)
            return visitParExpr((ParExpr) n);
        if (n instanceof Assign)
            return visitAssign((Assign) n);
        // We should be using interfaces (e.g., X10Binary, X10Unary) instead, but
        // (a) there is no X10Unary, and (b) the method name functions are only
        // available on concrete classes anyway.
        if (n instanceof X10Binary_c)
            return visitBinary((X10Binary_c) n);
        if (n instanceof X10Unary_c)
            return visitUnary((X10Unary_c) n);
        if (n instanceof X10Cast)
            return visitCast((X10Cast) n);
        if (n instanceof X10Instanceof)
            return visitInstanceof((X10Instanceof) n);
        return n;
    }

    protected Expr getLiteral(Position pos, Type type, boolean val) {
        type = X10TypeMixin.baseType(type);
        if (xts.isBoolean(type)) {
            Type t = xts.Boolean();
            try {
                t = X10TypeMixin.addSelfBinding(t, val ? xts.TRUE() : xts.FALSE());
            } catch (XFailure e) { }
            return xnf.BooleanLit(pos, val).type(t);
        }
        throw new InternalCompilerError(pos, "Unknown literal type: "+type);
    }

    protected Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, xts.Runtime(), name,  returnType, context());
    }

    /**
     * Remove parenthesized expressions.
     */
    protected Expr visitParExpr(ParExpr e) {
        return e.expr();
    }

    // desugar binary operators
    private Expr visitBinary(X10Binary_c n) throws SemanticException {
//        Position pos = n.position();

        Call c = X10Binary_c.desugarBinaryOp(n, this);
        if (c != null) {
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            if (mi.error() != null)
                throw mi.error();
            return c;
        }

        return n;
    }

    /**
     * Same as xnf.Assign(...), but also set the appropriate type objects, which the
     * node factory screws up.
     * @throws SemanticException
     */
    protected Assign assign(Position pos, Expr e, Assign.Operator asgn, Expr val) throws SemanticException {
        Assign a = (Assign) xnf.Assign(pos, e, asgn, val).type(e.type());
        if (a instanceof FieldAssign) {
            assert (e instanceof Field);
            assert ((Field) e).fieldInstance() != null;
            a = ((FieldAssign) a).fieldInstance(((Field)e).fieldInstance());
        } else if (a instanceof SettableAssign) {
            assert (e instanceof X10Call);
            X10Call call = (X10Call) e;
            List<Expr> args = CollectionUtil.append(Collections.singletonList(val), call.arguments());
            X10Call n = xnf.X10Call(pos, call.target(), nf.Id(pos, SettableAssign.SET), call.typeArguments(), args);
            n = (X10Call) n.del().disambiguate(this).typeCheck(this).checkConstants(this);
            X10MethodInstance smi = n.methodInstance();
            X10MethodInstance ami = call.methodInstance();
//            List<Type> aTypes = new ArrayList<Type>(ami.formalTypes());
//            aTypes.add(0, ami.returnType()); // rhs goes before index
//            MethodInstance smi = xts.findMethod(ami.container(),
//                    xts.MethodMatcher(ami.container(), SET, aTypes, context));
            a = ((SettableAssign) a).methodInstance(smi);
            a = ((SettableAssign) a).applyMethodInstance(ami);
        }
        return a;
    }

    private Expr getLiteral(Position pos, Type type, long val) throws SemanticException {
        type = X10TypeMixin.baseType(type);
        Expr lit = null;
        if (xts.isIntOrLess(type)) {
            lit = xnf.IntLit(pos, IntLit.INT, val);
        } else if (xts.isLong(type)) {
            lit = xnf.IntLit(pos, IntLit.LONG, val);
        } else if (xts.isUInt(type)) {
            lit = xnf.IntLit(pos, IntLit.UINT, val);
        } else if (xts.isULong(type)) {
            lit = xnf.IntLit(pos, IntLit.ULONG, val);
        } else if (xts.isFloat(type)) {
            lit = xnf.FloatLit(pos, FloatLit.FLOAT, val);
        } else if (xts.isDouble(type)) {
            lit = xnf.FloatLit(pos, FloatLit.DOUBLE, val);
        } else if (xts.isChar(type)) {
            // Don't want to cast
            return (Expr) xnf.IntLit(pos, IntLit.INT, val).typeCheck(this);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
        lit = (Expr) lit.typeCheck(this);
        if (!xts.isSubtype(lit.type(), type)) {
            lit = xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, type), lit,
                    Converter.ConversionType.PRIMITIVE).type(type);
        }
        return lit;
    }

    // ++x -> x+=1 or --x -> x-=1
    private Expr unaryPre(Position pos, X10Unary_c.Operator op, Expr e) throws SemanticException {
        Type ret = e.type();
        Expr one = getLiteral(pos, ret, 1);
        Assign.Operator asgn = (op == X10Unary_c.PRE_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        Expr a = assign(pos, e, asgn, one);
        a = visitAssign((Assign) a);
        return a;
    }

    // x++ -> (x+=1)-1 or x-- -> (x-=1)+1
    private Expr unaryPost(Position pos, X10Unary_c.Operator op, Expr e) throws SemanticException {
        Type ret = e.type();
        Expr one = getLiteral(pos, ret, 1);
        Assign.Operator asgn = (op == X10Unary_c.POST_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        X10Binary_c.Operator bin = (op == X10Unary_c.POST_INC) ? X10Binary_c.SUB : X10Binary_c.ADD;
        Expr incr = assign(pos, e, asgn, one);
        incr = visitAssign((Assign) incr);
        return visitBinary((X10Binary_c) xnf.Binary(pos, incr, bin, one).type(ret));
    }

    // desugar unary operators
    private Expr visitUnary(X10Unary_c n) throws SemanticException {
        Position pos = n.position();

//        Expr left = n.expr();
//        Type l = left.type();
        X10Unary_c.Operator op = n.operator();

        if (op == X10Unary_c.PRE_DEC || op == X10Unary_c.PRE_INC) {
            return unaryPre(pos, op, n.expr());
        }
        if (op == X10Unary_c.POST_DEC || op == X10Unary_c.POST_INC) {
            return unaryPost(pos, op, n.expr());
        }

        Call c = X10Unary_c.desugarUnaryOp(n, this);
        if (c != null) {
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            if (mi.error() != null)
                throw mi.error();
            return c;
        }

        return n;
    }

    // x++; -> ++x; or x--; -> --x; (to avoid creating an extra closure)
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

    private Expr visitAssign(Assign n) throws SemanticException {
        if (n instanceof SettableAssign)
            return visitSettableAssign((SettableAssign) n);
        if (n instanceof LocalAssign)
            return visitLocalAssign((LocalAssign) n);
        if (n instanceof FieldAssign)
            return visitFieldAssign((FieldAssign) n);
        return n;
    }

    // x op=v -> x = x op v
    private Expr visitLocalAssign(LocalAssign n) throws SemanticException { 
        Position pos = n.position();
        if (n.operator() == Assign.ASSIGN) return n;
        X10Binary_c.Operator op = n.operator().binaryOperator();
        Local left = (Local) n.left();
        Expr right = n.right();
        Type R = left.type();
        Expr val = visitBinary((X10Binary_c) xnf.Binary(pos, left, op, right).type(R));
        return assign(pos, left, Assign.ASSIGN, val);
    }

    // T.f op=v -> T.f = T.f op v or e.f op=v -> ((x:E,y:T)=>x.f=x.f op y)(e,v)
    protected Expr visitFieldAssign(FieldAssign n) throws SemanticException { 
        Position pos = n.position();
        if (n.operator() == Assign.ASSIGN) return n;
        X10Binary_c.Operator op = n.operator().binaryOperator();
        Field left = (Field) n.left();
        Expr right = n.right();
        Type R = left.type();
        if (left.flags().isStatic()) {
            Expr val = visitBinary((X10Binary_c) xnf.Binary(pos, left, op, right).type(R));
            return assign(pos, left, Assign.ASSIGN, val);
        }
        Expr e = (Expr) left.target();
        Type E = e.type();
        List<Formal> parms = new ArrayList<Formal>();
        Name xn = Name.make("x");
        LocalDef xDef = xts.localDef(pos, xts.Final(), Types.ref(E), xn);
        Formal x = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, E), xnf.Id(pos, xn)).localDef(xDef);
        parms.add(x);
        Name yn = Name.make("y");
        Type T = right.type();
        LocalDef yDef = xts.localDef(pos, xts.Final(), Types.ref(T), yn);
        Formal y = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, T), xnf.Id(pos, yn)).localDef(yDef);
        parms.add(y);
        Expr lhs = xnf.Field(pos,
                xnf.Local(pos, xnf.Id(pos, xn)).localInstance(xDef.asInstance()).type(E),
                xnf.Id(pos, left.name().id())).fieldInstance(left.fieldInstance()).type(R);
        Expr val = visitBinary((X10Binary_c) xnf.Binary(pos,
                lhs, op, xnf.Local(pos, xnf.Id(pos, yn)).localInstance(yDef.asInstance()).type(T)).type(R));
        Expr res = assign(pos, lhs, Assign.ASSIGN, val);
        Block body = xnf.Block(pos, xnf.Return(pos, res));
        Closure c = synth.makeClosure(pos, R, parms, body, context());
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        List<Expr> args = new ArrayList<Expr>();
        args.add(0, e);
        args.add(right);
        return xnf.ClosureCall(pos, c, args).closureInstance(ci).type(R);
    }

    // a(i)=v -> a.set(v, i) or a(i)op=v -> ((x:A,y:I,z:T)=>x.set(x.apply(y) op z,y))(a,i,v)
    protected Expr visitSettableAssign(SettableAssign n) throws SemanticException {
        Position pos = n.position();
        MethodInstance mi = n.methodInstance();
        List<Expr> args = new ArrayList<Expr>(n.index());
        if (n.operator() == Assign.ASSIGN) {
            // FIXME: this changes the order of evaluation, (a,i,v) -> (a,v,i)!
            args.add(0, n.right());
            return xnf.Call(pos, n.array(), xnf.Id(pos, mi.name()),
                    args).methodInstance(mi).type(mi.returnType());
        }
        X10Binary_c.Operator op = n.operator().binaryOperator();
        X10Call left = (X10Call) n.left();
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
        assert (xts.isSubtype(ami.returnType(), T, context()));
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
        Closure c = synth.makeClosure(pos, T, parms, block, context());
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
                if (n instanceof X10Special && ((X10Special) n).kind() == X10Special.SELF)
                    return by.get(0);
                return n;
            }
        };
        Expr left = null;
        for (Expr clause : clauses) {
            Expr right = (Expr) clause.visit(subst);
            right = (Expr) right.visit(this);
            if (left == null)
                left = right;
            else {
                left = xnf.Binary(pos, left, X10Binary_c.COND_AND, right).type(xts.Boolean());
                try {
                    left = visitBinary((X10Binary_c) left);
                } catch (SemanticException e) {
                    assert false : "Unexpected exception when typechecking "+left+": "+e;
                }
            }
        }
        return left;
    }

    private DepParameterExpr getClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            CConstraint c = X10TypeMixin.xclause(t);
            if (c == null || c.valid())
                return null;
            XConstrainedTerm here = context().currentPlaceTerm();
            if (here != null && here.term() instanceof XVar) {
                try {
                    c = c.substitute(PlaceChecker.here(), (XVar) here.term());
                } catch (XFailure e) { }
            }
            DepParameterExpr res = xnf.DepParameterExpr(tn.position(), new Synthesizer(xnf, xts).makeExpr(c, tn.position()));
            res = (DepParameterExpr) res.visit(new X10TypeBuilder(job, xts, xnf)).visit(new X10TypeChecker(job, xts, xnf, job.nodeMemo()).context(context().pushDepType(tn.typeRef())));
            return res;
        }
        throw new InternalCompilerError("Unknown type node type: "+tn.getClass(), tn.position());
    }

    private TypeNode stripClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            return xnf.CanonicalTypeNode(tn.position(), X10TypeMixin.baseType(t));
        }
        throw new InternalCompilerError("Unknown type node type: "+tn.getClass(), tn.position());
    }

    // e as T{c} -> ((x:T):T{c}=>{if (x!=null&&!c[self/x]) throwCCE(); return x;})(e as T)
    private Expr visitCast(X10Cast n) throws SemanticException {
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
        if (xts.isSubtype(t, xts.Object(), context())) {
            Expr nonnull = xnf.Binary(pos, xl, X10Binary_c.NE, xnf.NullLit(pos).type(xts.Null())).type(xts.Boolean());
            cond = xnf.Binary(pos, nonnull, X10Binary_c.COND_AND, cond).type(xts.Boolean());
        }
        Type ccet = xts.ClassCastException();
        CanonicalTypeNode CCE = xnf.CanonicalTypeNode(pos, ccet);
        Expr msg = xnf.StringLit(pos, ot.toString()).type(xts.String());
        X10ConstructorInstance ni = xts.findConstructor(ccet, xts.ConstructorMatcher(ccet, Collections.singletonList(xts.String()), context()));
        Expr newCCE = xnf.New(pos, CCE, Collections.singletonList(msg)).constructorInstance(ni).type(ccet);
        Stmt throwCCE = xnf.Throw(pos, newCCE);
        Stmt check = xnf.If(pos, cond, throwCCE);
        Block body = xnf.Block(pos, check, xnf.Return(pos, xl));
        Closure c = synth.makeClosure(pos, ot, Collections.singletonList(x), body, context());
        Expr cast = xnf.X10Cast(pos, tn, e, Converter.ConversionType.CHECKED).type(t);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        return xnf.ClosureCall(pos, c, Collections.singletonList(cast)).closureInstance(ci).type(ot);
    }

    // e instanceof T{c} -> ((x:F)=>x instanceof T && c[self/x as T])(e)
    private Expr visitInstanceof(X10Instanceof n) {
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
        Expr cast = xnf.X10Cast(pos, tn, xl, Converter.ConversionType.CHECKED).type(tn.type());
        List<Expr> condition = depClause.condition();
        Expr cond = conjunction(depClause.position(), condition, cast);
        Expr rval = xnf.Binary(pos, iof, X10Binary_c.COND_AND, cond).type(xts.Boolean());
        Block body = xnf.Block(pos, xnf.Return(pos, rval));
        Closure c = synth.makeClosure(pos, xts.Boolean(), Collections.singletonList(x), body, context());
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        return xnf.ClosureCall(pos, c, Collections.singletonList(e)).closureInstance(ci).type(xts.Boolean());
    }

    public static class Substitution<T extends Node> extends NodeVisitor {
        protected final List<T> by;
        private final Class<T> cz;
        public Substitution(Class<T> cz, List<T> by) {
            this.cz = cz;
            this.by = by;
        }
        @SuppressWarnings("unchecked") // Casting to a generic type parameter
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (cz.isInstance(n))
                return subst((T)n);
            return n;
        }
        protected T subst(T n) {
            return n;
        }
    }
}

