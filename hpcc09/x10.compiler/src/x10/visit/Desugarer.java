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
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Closure;
import x10.ast.Closure_c;
import x10.ast.Finish;
import x10.ast.ForEach;
import x10.ast.Future;
import x10.ast.Here;
import x10.ast.Next;
import x10.ast.SettableAssign_c;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10Formal;
import x10.ast.X10NodeFactory;
import x10.ast.X10Unary_c;
import x10.constraint.XRoot;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
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
        synth = new Synthesizer(xnf,xts);
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
        return n;
    }

    private Expr visitFuture(Future f) throws SemanticException {
        return visitRemoteClosure(f, EVAL_FUTURE, f.place());
    }

    private Expr visitAtExpr(AtExpr e) throws SemanticException {
        return visitRemoteClosure(e, EVAL_AT, e.place());
    }

    
    private Expr visitRemoteClosure(Closure c, Name implName, Expr place) throws SemanticException {
        Position pos = c.position();
    	if (xts.isImplicitCastValid(place.type(), xts.Ref(), context)) {
        	place = synth.makeFieldAccess(pos,place, Name.make("location"), xContext());
        }
    	
        List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { c.returnType() });
        Position bPos = c.body().position();
        ClosureDef cDef = c.closureDef().position(bPos);
        Expr closure = xnf.Closure(c, bPos)
            .closureDef(cDef)
        	.type(xts.closureAnonymousClassDef(cDef).asType());
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
      	if (xts.isImplicitCastValid(place.type(), xts.Ref(), context)) {
          	place = synth.makeFieldAccess(pos,place, Name.make("location"), xContext());
          }
        Closure closure = 
        	synth.makeClosure(body.position(), xts.Void(),  synth.toBlock(body), xContext());
        Stmt result = xnf.Eval(pos,
        		synth.makeStaticCall(pos, xts.Runtime(), RUN_AT, 
        				Arrays.asList(new Expr[] { place, closure }), xts.Void(), 
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
        return async(pos, a.body(), a.clocks(), a.place());
    }
    private Stmt async(Position pos, Stmt body, List<Expr> clocks, Expr place) throws SemanticException {
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
        Expr index = xnf.Local(bpos,
                xnf.Id(bpos, formal.name().id()))
                .localInstance(formal.localDef().asInstance())
                .type(fType);
        if (formal.isUnnamed()) {
            ArrayList<Expr> vars = new ArrayList<Expr>();
            for (LocalDef ld : formal.localInstances()) {
                vars.add(xnf.Local(bpos,
                        nf.Id(bpos, ld.name()))
                        .localInstance(ld.asInstance())
                        .type(ld.type().get()));
            }
            Type intRail = xts.ValRail(xts.Int());
            MethodInstance cnv = xts.findMethod(fType,
                    xts.MethodMatcher(fType, CONVERT_IMPLICITLY,
                    		Collections.singletonList(intRail), context));
            assert (cnv.flags().isStatic());
            index =
                xnf.Call(bpos, xnf.CanonicalTypeNode(bpos, fType), 
                		xnf.Id(bpos, CONVERT_IMPLICITLY),
                        xnf.Tuple(bpos, vars).type(intRail)).methodInstance(cnv).type(fType);
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
}
