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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Catch;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
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
import polyglot.visit.TypeBuilder;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.FunctionTypeNode;
import x10.ast.Future;
import x10.ast.Here;
import x10.ast.Next;
import x10.ast.Offer;
import x10.ast.ParExpr;
import x10.ast.Resume;
import x10.ast.SettableAssign;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof;
import x10.ast.X10IntLit_c;
import x10.ast.X10New;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.emitter.Emitter;
import x10.extension.X10Ext;
import x10.extension.X10Ext_c;
import x10.types.ClosureDef;
import x10.types.ConstrainedType;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.util.ClosureSynthesizer;
import x10.util.Synthesizer;
import x10.util.synthesizer.InstanceCallSynth;
import x10.extension.X10Ext;

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
    //Collecting Finish Use: store reducer
    private static Stack<FinishExpr> reducerS = new Stack<FinishExpr>();
    private static Stack<Local> clockStack = new Stack<Local>();
    private static int flag = 0;

    private static Name getTmp() {
        return Name.make("__desugarer__var__" + (count++) + "__");
    }

    protected X10Context xContext() { return (X10Context) context;}
    private static final Name RUN_AT = Name.make("runAt");
    private static final Name EVAL_AT = Name.make("evalAt");
    private static final Name EVAL_FUTURE = Name.make("evalFuture");
    private static final Name RUN_ASYNC = Name.make("runAsync");
    private static final Name RUN_UNCOUNTED_ASYNC = Name.make("runUncountedAsync");
    private static final Name HERE = Name.make("here");
    private static final Name HERE_INT = Name.make("hereInt");
    private static final Name NEXT = Name.make("next");
    private static final Name RESUME = Name.make("resume");
    
    private static final Name LOCK = Name.make("lock");
    private static final Name AWAIT = Name.make("await");
    private static final Name RELEASE = Name.make("release");
    private static final Name ENTER_ATOMIC = Name.make("enterAtomic");
    private static final Name ENSURE_NOT_IN_ATOMIC = Name.make("ensureNotInAtomic");
    private static final Name ENTER_ATOMIC_WHEN = Name.make("enterAtomicWhen");
    private static final Name EXIT_ATOMIC = Name.make("exitAtomic");
    
    private static final Name START_FINISH = Name.make("startFinish");
    private static final Name PUSH_EXCEPTION = Name.make("pushException");
    private static final Name STOP_FINISH = Name.make("stopFinish");
    private static final Name PLACES = Name.make("places");
    private static final Name RESTRICTION = Name.make("restriction");
    private static final Name CONVERT = Converter.operator_as;
    private static final Name CONVERT_IMPLICITLY = Converter.implicit_operator_as;
    private static final Name DIST = Name.make("dist");
    //added for scalable finish
    private static final Name START_LOCAL_FINISH = Name.make("startLocalFinish");
    private static final Name START_SIMPLE_FINISH = Name.make("startSimpleFinish");
    public Node override(Node parent, Node n) { 
    	if (n instanceof Finish) {
    		Finish finish = (Finish) n;
    		if (! finish.clocked())
    			// Follow normal procedure
    			return null;
    		// Translate clocked finish S ==> 
    		// var clock_??1:Clock=null; 
    		// finish 
    		// try { 
    		//   val clock_??2 = Clock.make(); 
    		//   clock_??1=clock_??2; 
    		//   S; //--> nested clocked async T ==> async clocked(clock_??2) T
    		//  } finally {
    		//    clock_???.drop();
    		//  }
    		// TODO: Simplify this to finish { val clock?? = Clock.make(); try { S} finally{ clock??.drop();}}
    		X10Context xc = (X10Context) context;
    		X10TypeSystem xts = (X10TypeSystem) ts;
    		Position pos = finish.position();
    		Name name = ((X10Context) context).makeFreshName("clock");
    		Flags flags = Flags.FINAL;
    		Type type = xts.Clock();
    		
    		final Name varName = xc.getNewVarName();
			final LocalDef li = xts.localDef(pos, flags, Types.ref(type), varName);
			final Id varId = xnf.Id(pos, varName);
			final Local ldRef = (Local) xnf.Local(pos, varId).localInstance(li.asInstance()).type(type);
			clockStack.push(ldRef);
			
			final Name outerVarName = xc.getNewVarName();
			final LocalDef outerLi = xts.localDef(pos, flags, Types.ref(type), outerVarName);
			final Id outerVarId = xnf.Id(pos, outerVarName);
			final Local outerLdRef = (Local) xnf.Local(pos, outerVarId).localInstance(outerLi.asInstance()).type(type);

			try {
				Expr clock = synth.makeStaticCall(pos, type, Name.make("make"), type, xc);
				final TypeNode tn = xnf.CanonicalTypeNode(pos, type);
				Expr nullLit = xnf.NullLit(pos).type(type);
				final LocalDecl outerLd = xnf.LocalDecl(pos, xnf.FlagsNode(pos, Flags.NONE), tn, outerVarId, nullLit).localDef(outerLi);
				
				Block block = synth.toBlock(finish.body());
				final LocalDecl ld = xnf.LocalDecl(pos, xnf.FlagsNode(pos, flags), tn, varId, clock).localDef(li);
				Stmt assign = xnf.Eval(pos, assign(pos, outerLdRef, Assign.ASSIGN, ldRef));
				block = block.prepend(assign);
				block = block.prepend(ld);
				Block drop = xnf.Block(pos,xnf.Eval(pos, new InstanceCallSynth(xnf, (X10Context) context, pos, outerLdRef, "drop").genExpr()));
				Stmt stm1 = nf.Try(pos, block, Collections.<Catch>emptyList(), drop);
				Node result = visitEdgeNoOverride(parent, nf.Block(pos, outerLd, xnf.Finish(pos, stm1, false)));
				return result;
			} catch (SemanticException z) {
				return null;
			}
    	}
    	// handle async at(p) S and treat it as the old async(p) S.
    	if (n instanceof Async) {
    		Async async = (Async) n;
    		Stmt body = async.body();
    		AtStmt atStm = toAtStmt(body);
    		if (atStm==null)
    			return null;
    		Expr place = atStm.place(); 
    		if (xts.hasSameClassDef(X10TypeMixin.baseType(place.type()), xts.GlobalRef())) {
    			try {
    				place = synth.makeFieldAccess(async.position(),place, xts.homeName(), xContext());
    			} catch (SemanticException e) {
    			}
    		}
    		List<Expr> clocks = async.clocks();
    		place = (Expr) visitEdgeNoOverride(atStm, place);
    		body = (Stmt) visitEdgeNoOverride(atStm, atStm.body());
    		if (clocks != null && ! clocks.isEmpty()) {
    			List<Expr> nclocks = new ArrayList<Expr>();
    			for (Expr c : clocks) {
    				nclocks.add((Expr) visitEdgeNoOverride(async, c));
    			}
    			clocks =nclocks;
    		}
    		try {
    			return visitAsyncPlace(async, place, body);
    		} catch (SemanticException z) {
    			return null;
    		}
    	}
        if (n instanceof Eval) {
            try {
                Stmt s = visitEval((Eval) n);
                flag = 1;
                return visitEdgeNoOverride(parent, s);
            }
            catch (SemanticException e) {
                return null;
            }
        }

        return null;
    }

    //Collecting Finish Use : store reducer when enter finishR
    public NodeVisitor superEnter(Node parent, Node n) {
        if (n instanceof LocalDecl){
            LocalDecl f = (LocalDecl) n;
            if (f.init() instanceof FinishExpr) {
                reducerS.push((FinishExpr) f.init());
            }
        }
        if (n instanceof Eval) {
            if (((Eval) n).expr() instanceof Assign) {
                Assign f = (Assign) ((Eval)n).expr();
                Expr right = f.right();
                if (right instanceof FinishExpr) {
                    reducerS.push((FinishExpr) f.right());
                }
            }
        }
        if (n instanceof Return) {
            Return f = (Return) n;
            if (f.expr() instanceof FinishExpr) {
                reducerS.push((FinishExpr) f.expr());
            }
        }

        return super.superEnter(parent, n);
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
        if (n instanceof Offer)
            return visitOffer((Offer) n);
        if (n instanceof Return)
            return visitReturn((Return) n);
        if (n instanceof AtEach)
            return visitAtEach((AtEach) n);
        if (n instanceof Eval)
            return visitEval((Eval) n);
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
        if (n instanceof LocalDecl)
            return visitLocalDecl((LocalDecl) n);
        if (n instanceof Resume)
            return visitResume((Resume) n);
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
            			Collections.<TypeNode>emptyList(),
            			Collections.<Expr>emptyList(),
            			xts.Place(),
            			Collections.<Type>emptyList(),
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

    private AtStmt toAtStmt(Stmt body) {
    	if ((body instanceof AtStmt)) {
    		return (AtStmt) body;
    	}
    	if (body instanceof Block) {
    		Block block = (Block) body;
    		if (block.statements().size()==1) {
    			body = block.statements().get(0);
    			if ((body instanceof AtStmt)) {
    				return 	(AtStmt) body;
    			}
    		}
    	}
    	return null;
    }
    
    private List<Expr> clocks(boolean clocked, List<Expr> clocks) {
    	if (! clocked)
    		return clocks;
    	if (clocks == null)
    		clocks = new ArrayList<Expr>();
    	clocks.add(clockStack.peek());
    	return clocks;
    }
    // Begin asyncs
    // rewrite @Uncounted async S, with special translation for @Uncounted async at (p) S.
    private Stmt visitAsync(Node old, Async a) throws SemanticException {
    	List<Expr> clocks = clocks(a.clocked(), a.clocks());
        Position pos = a.position();
        X10Ext ext = (X10Ext) a.ext();
        List<X10ClassType> refs = Emitter.annotationsNamed(xts, a, REF);
        if (isUncountedAsync(xts,a)) {
        	if (old instanceof Async)
            	 return uncountedAsync(pos, a.body());
        }
        if (old instanceof Async)
            return async(pos, a.body(), clocks, refs);
        Stmt specializedAsync = specializeAsync(a, null, a.body());
        if (specializedAsync != null)
            return specializedAsync;
        return async(pos, a.body(), clocks,  refs);
    }
    // Begin asyncs
    // rewrite @Uncounted async S, with special translation for @Uncounted async at (p) S.
    private Stmt visitAsyncPlace(Async a, Expr place, Stmt body) throws SemanticException {
    	List<Expr> clocks = clocks(a.clocked(), a.clocks());
        Position pos = a.position();
        List<X10ClassType> refs = Emitter.annotationsNamed(xts, a, REF);
        if (isUncountedAsync(xts,a)) {
            return uncountedAsync(pos, body, place);
        }
        Stmt specializedAsync = specializeAsync(a, place, body);
        if (specializedAsync != null)
            return specializedAsync;
        return async(pos, body, clocks,   place, refs);
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
    private static final QName REF = QName.make("x10.compiler.Ref");
    private static final QName UNCOUNTED = QName.make("x10.compiler.Uncounted");
    private static final QName REMOTE_OPERATION = QName.make("x10.compiler.RemoteOperation");

    public static boolean isUncountedAsync(X10TypeSystem xts, Async a) {
        return Emitter.hasAnnotation(xts, a, UNCOUNTED);
    }

    /**
     * Recognize the following pattern:
     * <pre>
     * @Immediate async at(p) {
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
    private Stmt specializeAsync(Async a, Expr p, Stmt body) throws SemanticException {
        if (!Emitter.hasAnnotation(xts, a, IMMEDIATE))
            return null;
        if (a.clocks().size() != 0)
            return null;
        
        if (body instanceof Block) {
            List<Stmt> stmts = ((Block) body).statements();
            if (stmts.size() != 1)
                return null;
            body = stmts.get(0);
        }
        if (!(body instanceof Eval))
            return null;
        Expr e = ((Eval) body).expr();
        if (!(e instanceof SettableAssign))
            return null;
        SettableAssign sa = (SettableAssign) e;
        if (sa.operator() != Assign.BIT_XOR_ASSIGN)
            return null;
        List<Expr> is = sa.index();
        if (is.size() != 1)
            return null;
        Expr i = is.get(0);
        if (p instanceof X10New) {
            // TODO: make sure we calling the place constructor
            // TODO: decide between rail and place-local handle
            X10New n = (X10New) p;
            Expr q =  n.arguments().get(0);
            Expr r = sa.array();
            Expr v = sa.right();
            if (/*!isGloballyAvailable(r) || */!isGloballyAvailable(i) || !isGloballyAvailable(v))
                return null;
    /*        List<Type> ta = ((X10ClassType) X10TypeMixin.baseType(r.type())).typeArguments();
            if (!v.type().isLong() || !xts.isRailOf(r.type(), xts.Long()))
                return null;
            if (!PlaceChecker.isAtPlace(r, p, xContext()))
                return null;
    */
            ClassType RemoteOperation = (ClassType) xts.typeForName(REMOTE_OPERATION);
            Position pos = a.position();
            List<Expr> args = new ArrayList<Expr>();
            Expr p1 = (Expr) leaveCall(null, q, this);
            args.add(p1);
            args.add((Expr) leaveCall(null, r, this));
            args.add((Expr) leaveCall(null, i, this));
            args.add((Expr) leaveCall(null, v, this));
            Stmt alt = xnf.Eval(pos, synth.makeStaticCall(pos, RemoteOperation, XOR, args, xts.Void(), xContext()));
            Expr cond = xnf.Binary(pos, q, X10Binary_c.EQ, call(pos, HERE_INT, xts.Int())).type(xts.Boolean());
            Stmt cns = a.body();
            return xnf.If(pos, cond, cns, alt);
        } else {
            Expr r = sa.array();
            Expr v = sa.right();
            if (/*!isGloballyAvailable(r) || */!isGloballyAvailable(i) || !isGloballyAvailable(v))
                return null;
    /*        List<Type> ta = ((X10ClassType) X10TypeMixin.baseType(r.type())).typeArguments();
            if (!v.type().isLong() || !xts.isRailOf(r.type(), xts.Long()))
                return null;
            if (!PlaceChecker.isAtPlace(r, p, xContext()))
                return null;
    */
            ClassType RemoteOperation = (ClassType) xts.typeForName(REMOTE_OPERATION);
            Position pos = a.position();
            List<Expr> args = new ArrayList<Expr>();
            Expr p1 = (Expr) leaveCall(null, p, this);
            args.add(p1);
            args.add((Expr) leaveCall(null, r, this));
            args.add((Expr) leaveCall(null, i, this));
            args.add((Expr) leaveCall(null, v, this));
            Stmt alt = xnf.Eval(pos, synth.makeStaticCall(pos, RemoteOperation, XOR, args, xts.Void(), xContext()));
            Expr cond = xnf.Binary(pos, p, X10Binary_c.EQ, call(pos, HERE, xts.Place())).type(xts.Boolean());
            Stmt cns = a.body();
            return xnf.If(pos, cond, cns, alt);
        }
    }

    private Stmt async(Position pos, Stmt body, List<Expr> clocks, Expr place, List<X10ClassType> annotations) throws SemanticException {
        if (xts.isImplicitCastValid(place.type(), xts.GlobalRef(), context)) {
            place = synth.makeFieldAccess(pos,place, xts.homeName(), xContext());
        }
        if (clocks.size() == 0)
        	return async(pos, body, place, annotations);
        Type clockRailType = X10TypeMixin.makeArrayRailOf(xts.Clock(), pos);
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);

        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { place, clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { xts.Place(), clockRailType})),
                             body, annotations);
    }

    private Stmt async(Position pos, Stmt body, Expr place, List<X10ClassType> annotations) throws SemanticException {
    	List<Expr> l = new ArrayList<Expr>(1);
    	l.add(place);
    	List<Type> t = new ArrayList<Type>(1);
    	t.add(xts.Place());
    	return makeAsyncBody(pos, l, t, body, annotations);
    }

    private Stmt async(Position pos, Stmt body, List<Expr> clocks, List<X10ClassType> annotations) throws SemanticException {
        if (clocks.size() == 0)
        	return async(pos, body, annotations);
        Type clockRailType = X10TypeMixin.makeArrayRailOf(xts.Clock(), pos);
        Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { clockRailType})), body, annotations);
    }

    private Stmt async(Position pos, Stmt body, List<X10ClassType> annotations) throws SemanticException {
        return makeAsyncBody(pos, new LinkedList<Expr>(),
                new LinkedList<Type>(), body, annotations);
    }

    private Stmt makeAsyncBody(Position pos, List<Expr> exprs, List<Type> types, Stmt body, List<X10ClassType> annotations) throws SemanticException {
        Closure closure = synth.makeClosure(body.position(), xts.Void(),
                synth.toBlock(body), xContext(), annotations);
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        Stmt result = xnf.Eval(pos,
                synth.makeStaticCall(pos, xts.Runtime(), RUN_ASYNC, exprs,
                        xts.Void(), types, xContext()));
        return result;
    }

    private Stmt uncountedAsync(Position pos, Stmt body, Expr place) throws SemanticException {
    	List<Expr> l = new ArrayList<Expr>(1);
    	l.add(place);
    	List<Type> t = new ArrayList<Type>(1);
    	t.add(xts.Place());
    	return makeUncountedAsyncBody(pos, l, t, body);
    }

    private Stmt uncountedAsync(Position pos, Stmt body) throws SemanticException {
        return makeUncountedAsyncBody(pos, new LinkedList<Expr>(),
                new LinkedList<Type>(), body);
    }

    private Stmt makeUncountedAsyncBody(Position pos, List<Expr> exprs, List<Type> types, Stmt body) throws SemanticException {
        Closure closure = synth.makeClosure(body.position(), xts.Void(),
                synth.toBlock(body), xContext());
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        Stmt result = xnf.Eval(pos,
                synth.makeStaticCall(pos, xts.Runtime(), RUN_UNCOUNTED_ASYNC, exprs,
                        xts.Void(), types, xContext()));
        return result;
    }
    // end Async


    // here -> Runtime.here()
    private Expr visitHere(Here h) throws SemanticException {
        Position pos = h.position();
        return call(pos, HERE, xts.Place());
    }

    // next; -> Runtime.next();
    private Stmt visitNext(Next n) throws SemanticException {
        Position pos = n.position();
        return xnf.Eval(pos, call(pos, NEXT, xts.Void()));
    }
    
    // next; -> Runtime.next();
    private Stmt visitResume(Resume n) throws SemanticException {
        Position pos = n.position();
        return xnf.Eval(pos, call(pos, RESUME, xts.Void()));
    }

    // atomic S; -> try { Runtime.enterAtomic(); S } finally { Runtime.exitAtomic(); }
    private Stmt visitAtomic(Atomic a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, ENTER_ATOMIC, xts.Void())), a.body());
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, EXIT_ATOMIC, xts.Void())));
        return xnf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    }

    // await(E); ->
    //    try { Runtime.enterAtomicWhen(); while (!E) Runtime.await(); } finally { Runtime.exitAtomic(); }
    private Stmt visitAwait(Await a) throws SemanticException {
        Position pos = a.position();
        return xnf.Try(pos,
        		xnf.Block(pos,
                		xnf.Eval(pos, call(pos, ENTER_ATOMIC_WHEN, xts.Void())),
                        xnf.While(pos,
                        		xnf.Unary(pos, a.expr(), Unary.NOT).type(xts.Boolean()),  // TODO: handle constraints (should be done in the synthesizer)
                        		xnf.Eval(pos, call(pos, AWAIT, xts.Void())))),
        		Collections.<Catch>emptyList(),
        		xnf.Block(pos,
        				xnf.Eval(pos, call(pos, EXIT_ATOMIC, xts.Void()))));
    }

    private Stmt wrap(Position pos, Stmt s) {
        return s.reachable() ? xnf.Block(pos, s, xnf.Break(pos)) : s;
    }

    private Expr getLiteral(Position pos, Type type, boolean val) {
        type = X10TypeMixin.baseType(type);
        if (xts.isBoolean(type)) {
            Type t = xts.Boolean();
            try {
                t = X10TypeMixin.addSelfBinding(t, val ? xts.TRUE() : xts.FALSE());
            } catch (XFailure e) { }
            return xnf.BooleanLit(pos, val).type(t);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
    }

    // when(E1) S1 or(E2) S2...; ->
    //    Runtime.ensureNotInAtomic();
    //    try { Runtime.enterAtomic();
    //          while (true) { if (E1) { S1; break; } if (E2) { S2; break; } ... Runtime.await(); }
    //    finally { Runtime.exitAtomic(); }
    private Stmt visitWhen(When w) throws SemanticException {
        Position pos = w.position();
        Block body = xnf.Block(pos, xnf.If(pos, w.expr(), wrap(pos, w.stmt())));
        for(int i=0; i<w.stmts().size(); i++) {
            body = body.append(xnf.If(pos, (Expr) w.exprs().get(i), wrap(pos, (Stmt) w.stmts().get(i))));
        }
        body = body.append(xnf.Eval(pos, call(pos, AWAIT, xts.Void())));
        Block tryBlock = xnf.Block(pos, 
        		xnf.Eval(pos, call(pos, ENTER_ATOMIC, xts.Void())),
        		xnf.While(pos, getLiteral(pos, xts.Boolean(), true), body));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, EXIT_ATOMIC, xts.Void())));
        return xnf.Block(pos, 
        		xnf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, xts.Void())),
        		xnf.Try(pos, 
        				tryBlock, Collections.<Catch>emptyList(), 
        				finallyBlock));
    }

    private Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, xts.Runtime(), name,  returnType, xContext());
    }

    /**
     * Recognize the following pattern:
     * <pre>
     * @Immediate finish S;
     * </pre>
     * where <tt>S</tt> is any statement,
     * and compile it into S followed by an optimized remote fence operation.
     * @param f the finish statement
     * @return a block consisting of S followed by the invocation of a remote fence operation, or null if no match
     * @throws SemanticException
     * TODO: move into a separate pass!
     */
    private Stmt specializeFinish(Finish f) throws SemanticException {
        if (!Emitter.hasAnnotation(xts, f, IMMEDIATE))
            return null;
        Position pos = f.position();
        ClassType target = (ClassType) xts.typeForName(REMOTE_OPERATION);
        List<Expr> args = new ArrayList<Expr>();
        return xnf.Block(pos, f.body(), xnf.Eval(pos, synth.makeStaticCall(pos, target, FENCE, args, xts.Void(), xContext())));
    }
    
    private int getPatternFromAnnotation(AnnotationNode a){
    	Ref<? extends Type> r = a.annotationType().typeRef();
		X10ParsedClassType xpct = (X10ParsedClassType) r.getCached();
		List<Expr> allProperties = xpct.propertyInitializers();
		Expr pattern = allProperties.get(3);
		if (pattern instanceof IntLit_c) {
			return (int) ((IntLit_c) pattern).value();
		}
		return 0;
    }
    /**
     * Recognize the following pattern:
     * @FinishAsync(,,,"local") which means all asyncs in this finish are in the same place as finish
     * @param f
     * @return a method call expression that invokes "startLocalFinish"
     * @throws SemanticException
     */
    private Expr specializedFinish2(Finish f) throws SemanticException {
        Position pos = f.position();
    	int p=0;
        Type annotation = (Type) xts.systemResolver().find(QName.make("x10.compiler.FinishAsync"));
        if (!((X10Ext) f.ext()).annotationMatching(annotation).isEmpty()) {
        	List<AnnotationNode> allannots = ((X10Ext)(f.ext())).annotations();
        	AnnotationNode a = null;
        	int p1 = 0;
        	int p2 = 0;
        	if(allannots.size()>0){
				if (allannots.size() > 1) {
					boolean isConsistent = true;
					for(int i=0;i<allannots.size()-1;i++){
						p1 = getPatternFromAnnotation(allannots.get(i));
						p2 = getPatternFromAnnotation(allannots.get(i+1));
						if(p1 != p2){
							isConsistent = false;
							break;
						}
					}
					if(!isConsistent){
						Report.report(0,"WARNING:compiler inferes different annotations from what the programer sets in "+job.source().name());
						if(Report.should_report("verbose", 1)){
							Report.report(5,"\tcompiler inferes "+p1);
							Report.report(5,"\tprogrammer annotates "+p2);
						}
					}
				}
				a = allannots.get(allannots.size()-1);
				if(Report.should_report("", 1)) 
					Report.report(1,a.toString());
				p = getPatternFromAnnotation(a);
				
        	}else{
        		Report.report(0,"annotation is not correct "+ allannots.size());
        	}
        }
        switch(p){
        case 1:return call(pos, START_LOCAL_FINISH, xts.Void());
        case 2:return call(pos, START_SIMPLE_FINISH, xts.Void());
        //TODO:more patterns can be filled here
        default:return call(pos, START_FINISH, xts.Void());
        }
    }
    // finish S; ->
    //    Runtime.ensureNotInAtomic();
    //    try { Runtime.startFinish(); S; }
    //    catch (t:Throwable) { Runtime.pushException(t); }
    //    finally { Runtime.stopFinish(); }
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
                xnf.Id(pos, PUSH_EXCEPTION), Collections.<TypeNode>emptyList(),
                Collections.singletonList(local)).methodInstance(mi).type(xts.Void());

        Block tryBlock = xnf.Block(pos, 
        		xnf.Eval(pos,specializedFinish2(f)), 
        		f.body());
        Catch catchBlock = xnf.Catch(pos, formal, xnf.Block(pos, xnf.Eval(pos, call)));
        Block finallyBlock = xnf.Block(pos, xnf.Eval(pos, call(pos, STOP_FINISH, xts.Void())));

        Try tcfBlock = xnf.Try(pos, tryBlock, Collections.singletonList(catchBlock), finallyBlock);

        // propagate async initialization info to backend
        X10Ext_c ext = (X10Ext_c) f.ext();
        if (ext.asyncInitVal != null) {
            tcfBlock = (Try)((X10Ext_c)tcfBlock.ext()).asyncInitVal(ext.asyncInitVal);
        }

        return xnf.Block(pos, 
        		xnf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, xts.Void())),
        		tcfBlock);
    }
    
    // x = finish (R) S; ->
    //    {
    //    val f = new Runtime.CollectingFinish(R);
    //    try { S; }
    //    catch (t:Throwable) { Runtime.pushException(t); }
    //    finally { x = f.stopFinishExpr(); }
    //    }
    private Stmt visitFinishExpr(Assign n, LocalDecl l, Return r) throws SemanticException {
    	FinishExpr f = null;
        if ((l==null) && (n!=null)&& (r == null)) {
                f = (FinishExpr) n.right();
        }
        if ((n==null) && (l!=null)&& (r==null)) {
                f = (FinishExpr) l.init();
        }
        if ((n==null) && (l==null)&& (r!=null)) {
                f = (FinishExpr) r.expr();
        }
    	
        Position pos = f.position();
        Expr reducer = f.reducer();
        
        // Begin Try Block Code
        Type reducerType = reducer.type();
        if (reducerType instanceof ConstrainedType) {
    		ConstrainedType ct = (ConstrainedType) reducerType;
    		reducerType = X10TypeMixin.baseType(Types.get(ct.baseType()));
        }

        // reducerType is "Reducible[T]", and reducerTarget is "T"
        // Parse out T
        Type reducerTarget = X10TypeMixin.reducerType(reducerType);
        assert reducerTarget!=null;
        
        Type coFinish = xts.load("x10.lang.Runtime.CollectingFinish");
        Type coFinishT = (((X10ParsedClassType)coFinish).typeArguments(Collections.singletonList(reducerTarget)));
        CanonicalTypeNode TTE = xnf.CanonicalTypeNode(pos, coFinishT);
        X10ConstructorInstance ti = xts.findConstructor(coFinishT, xts.ConstructorMatcher(coFinishT, Collections.singletonList(reducerType), context));
        Expr newCF = xnf.New(pos, TTE, Collections.singletonList(reducer)).constructorInstance(ti).type(coFinishT);

        Name tmp1 = getTmp();
        LocalDef lDef1 = xts.localDef(pos, xts.NoFlags(), Types.ref(coFinishT), tmp1);
        Expr local1 = xnf.Local(pos, xnf.Id(pos, tmp1)).localInstance(lDef1.asInstance()).type(coFinishT);
        final Flags flags = Flags.FINAL;   
        LocalDecl localDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, flags), xnf.CanonicalTypeNode(pos, coFinishT), xnf.Id(pos, tmp1)).localDef(lDef1);
        
        Expr a = assign(pos, local1, Assign.ASSIGN, newCF);
        Stmt s1 = xnf.Eval(pos, a);
        Block tryBlock = xnf.Block(pos,f.body());

        // Begin catch block
        Name tmp2 = getTmp();
        MethodInstance mi = xts.findMethod(xts.Runtime(),
                xts.MethodMatcher(xts.Runtime(), PUSH_EXCEPTION, Collections.singletonList(xts.Throwable()), context));
        LocalDef lDef = xts.localDef(pos, xts.NoFlags(), Types.ref(xts.Throwable()), tmp2);
        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                xnf.CanonicalTypeNode(pos, xts.Throwable()), xnf.Id(pos, tmp2)).localDef(lDef);
        Expr local = xnf.Local(pos, xnf.Id(pos, tmp2)).localInstance(lDef.asInstance()).type(xts.Throwable());
        Expr call = xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, xts.Runtime()),
                xnf.Id(pos, PUSH_EXCEPTION), Collections.<TypeNode>emptyList(),
                Collections.singletonList(local)).methodInstance(mi).type(xts.Void());
        Catch catchBlock = xnf.Catch(pos, formal, xnf.Block(pos, xnf.Eval(pos, call)));
        
        // Begin finally block
        Name STOPFINISHEXPR = Name.make("stopFinishExpr");
        Stmt returnS = null;
        if ((l==null) && (n!=null)&& (r==null)) {
        	Expr left = n.left().type(reducerTarget);
            Call instanceCall = synth.makeInstanceCall(pos, local1, STOPFINISHEXPR, Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList(), reducerTarget, Collections.<Type>emptyList(), xContext());
            Expr b = assign(pos, left, Assign.ASSIGN, instanceCall);
            returnS = xnf.Eval(pos, b);
      	}
        if ((n==null) && (l!=null) && (r==null)) {
            Expr local2 = xnf.Local(pos, l.name()).localInstance(l.localDef().asInstance()).type(reducerTarget);
        	Call instanceCall = synth.makeInstanceCall(pos, local1, STOPFINISHEXPR, Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList(), reducerTarget, Collections.<Type>emptyList(), xContext());
         	Expr b = assign(pos, local2, Assign.ASSIGN, instanceCall);
            returnS = xnf.Eval(pos, b);
        }
        if ((n==null) && (l==null) && (r!=null)) {
            Call instanceCall = synth.makeInstanceCall(pos, local1, STOPFINISHEXPR, Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList(), reducerTarget, Collections.<Type>emptyList(), xContext());
            returnS = xnf.X10Return(pos, instanceCall, true);
        }
        
        Block finalBlock = xnf.Block(pos, returnS);
        if(reducerS.size()>0) reducerS.pop();
        return xnf.Block(pos, localDecl, s1, xnf.Try(pos, tryBlock, Collections.singletonList(catchBlock), finalBlock));
    }
    //  offer e ->
    //  x10.lang.Runtime.CollectingFinish.offer(e);      
	private Stmt visitOffer(Offer n) throws SemanticException {		
    	Position pos = n.position();
    	Expr offerTarget = n.expr();
        Type expectType = null;
        if(reducerS.size()>0) {
            FinishExpr f = reducerS.peek();
            Expr reducer = f.reducer();
            Type reducerType = reducer.type();
            if (reducerType instanceof ConstrainedType) {
                ConstrainedType ct = (ConstrainedType) reducerType;
                reducerType = X10TypeMixin.baseType(Types.get(ct.baseType()));
            }
            X10ParsedClassType reducerTypeWithGenericType = null;
            Type thisType = reducerType;
            while(thisType != null) {
            //First check the reducerType itself is a reducible or not;
            //If not, it should be a class that implements reducible            
            if(xts.isReducible(((ClassType)thisType).def().asType())){
                //generic type case
                reducerTypeWithGenericType = (X10ParsedClassType) thisType;
                break;
            }
            else{ 
                //implement interface case
                for (Type t : xts.interfaces(thisType)) {
                    ClassType baseType = ((X10ParsedClassType)t).def().asType();
                    if(xts.isReducible(baseType)){
                        reducerTypeWithGenericType = (X10ParsedClassType) t;
                        break;
                    }
                }
            }
            thisType = xts.superClass(thisType);
            }
            
            assert(reducerTypeWithGenericType != null);
            //because Reducible type only has one argument, we could take it directly
            expectType = reducerTypeWithGenericType.typeArguments().get(0);
        }
        else {
            expectType = offerTarget.type();
        }
   	 
        CanonicalTypeNode CCE = xnf.CanonicalTypeNode(pos, expectType);
        TypeNode reducerA = (TypeNode) CCE;
        Expr newOfferTarget = xnf.X10Cast(pos, reducerA, offerTarget,Converter.ConversionType.CHECKED).type(reducerA.type());

    	Name OFFER = Name.make("offer");  
    	Type coFinish = xts.load("x10.lang.Runtime.CollectingFinish");
        Type coFinishT = (((X10ParsedClassType)coFinish).typeArguments(Collections.singletonList(expectType))); 	   	  	    	    	
    	Call call = synth.makeStaticCall(pos, coFinishT, OFFER, Collections.singletonList(offerTarget), xts.Void(), Collections.singletonList(expectType),  xContext());
    	
    	Stmt offercall = xnf.Eval(pos, call);     	
    	return offercall;		 
	}

    //handle finishR in return stmt:
    private Stmt visitReturn(Return n) throws SemanticException {
        if (n.expr() instanceof FinishExpr) {
            Stmt returnS = visitFinishExpr(null,null,n);
            return returnS;
        }

        return n;
    }

    private Stmt visitLocalDecl(LocalDecl n) throws SemanticException {
        if (n.init() instanceof FinishExpr) {
            Position pos = n.position();
            ArrayList<Stmt> sList = new ArrayList<Stmt>();
            sList.add(n.init(null));                      
            Stmt s = visitFinishExpr(null, n,null);
            sList.add(s);
            return xnf.StmtSeq(pos, sList);
        }
      	return n;
    }

    // ateach (p in D) S; ->
    //    { Runtime.ensureNotInAtomic(); val d = D.dist; for (p in d.places()) async (p) for (pt in d|here) async S; }
    private Stmt visitAtEach(AtEach a) throws SemanticException {
        Position pos = a.position();
        Position bpos = a.body().position();
        Name tmp = getTmp();

        Expr domain = a.domain();
        Type dType = domain.type();
        if (((X10TypeSystem_c) xts).isX10DistArray(dType)) {
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
        // Have to desugar some newly-created nodes
        Type pType = xts.Place();
        MethodInstance rmi = xts.findMethod(dType,
                xts.MethodMatcher(dType, RESTRICTION, Collections.singletonList(pType), context));
        Expr here = visitHere(xnf.Here(bpos));
        Expr dAtPlace = xnf.Call(bpos,
                xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                xnf.Id(bpos, RESTRICTION),
                here).methodInstance(rmi).type(rmi.returnType());
        Expr here1 = visitHere(xnf.Here(bpos));
        Stmt body = async(a.body().position(), a.body(), a.clocks(), here1, null);
        Stmt inner = xnf.ForLoop(pos, formal, dAtPlace, body).locals(formal.explode(this));
        MethodInstance pmi = xts.findMethod(dType,
                xts.MethodMatcher(dType, PLACES, Collections.<Type>emptyList(), context));
        Expr places = xnf.Call(bpos,
                xnf.Local(pos, xnf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                xnf.Id(bpos, PLACES)).methodInstance(pmi).type(pmi.returnType());
        Name pTmp = getTmp();
        LocalDef pDef = xts.localDef(pos, xts.Final(), Types.ref(pType), pTmp);
        Formal pFormal = xnf.Formal(pos, xnf.FlagsNode(pos, xts.Final()),
                xnf.CanonicalTypeNode(pos, pType), xnf.Id(pos, pTmp)).localDef(pDef);
        Stmt body1 = async(bpos, inner, a.clocks(),
                xnf.Local(bpos, xnf.Id(bpos, pTmp)).localInstance(pDef.asInstance()).type(pType), null);
        return xnf.Block(pos, 
        		xnf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, xts.Void())),
        		local, 
        		xnf.ForLoop(pos, pFormal, places, body1));
    }

    // desugar binary operators
    private Expr visitBinary(X10Binary_c n) throws SemanticException {
        Position pos = n.position();

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
            X10Call n = (X10Call) xnf.X10Call(pos, call.target(), nf.Id(pos, SettableAssign.SET), call.typeArguments(), CollectionUtil.append(Collections.singletonList(val), call.arguments()));
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
            lit = xnf.IntLit(pos, X10IntLit_c.UINT, val);
        } else if (xts.isULong(type)) {
            lit = xnf.IntLit(pos, X10IntLit_c.ULONG, val);
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
        if ((n.expr() instanceof Assign)&&(flag==1)) {
            Assign f = (Assign) n.expr();
            Expr right = f.right();
            if (right instanceof FinishExpr)
                return visitFinishExpr(f, null,null);
             
        }
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
        Closure c = synth.makeClosure(pos, R, parms, body, (X10Context) context);
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
        assert (xts.isSubtype(ami.returnType(), T, context));
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
            XConstrainedTerm here = ((X10Context) context).currentPlaceTerm();
            if (here != null && here.term() instanceof XVar) {
                try {
                    c = c.substitute(PlaceChecker.here(), (XVar) here.term());
                } catch (XFailure e) { }
            }
            DepParameterExpr res = xnf.DepParameterExpr(tn.position(), new Synthesizer(xnf, xts).makeExpr(c, tn.position()));
            res = (DepParameterExpr) res.visit(new TypeBuilder(job, xts, xnf)).visit(new X10TypeChecker(job, xts, xnf, job.nodeMemo()).context(((X10Context) context).pushDepType(tn.typeRef())));
            return res;
        } else {
            assert false : "Unknown type node type: "+tn.getClass();
        }
        return null;
    }

    private TypeNode stripClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            return xnf.CanonicalTypeNode(tn.position(), X10TypeMixin.baseType(t));
        } else {
            assert false : "Unknown type node type: "+tn.getClass();
        }
        return tn;
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
        if (xts.isSubtype(t, xts.Object(), context)) {
            Expr nonnull = xnf.Binary(pos, xl, X10Binary_c.NE, xnf.NullLit(pos).type(xts.Null())).type(xts.Boolean());
            cond = xnf.Binary(pos, nonnull, X10Binary_c.COND_AND, cond).type(xts.Boolean());
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
        Expr cast = xnf.X10Cast(pos, tn, e, Converter.ConversionType.CHECKED).type(t);
        X10MethodInstance ci = c.closureDef().asType().applyMethod();
        return xnf.ClosureCall(pos, c, Collections.singletonList(cast)).closureInstance(ci).type(ot);
    }

    // e instanceof T{c} -> ((x:F)=>x instanceof T && c[self/x as T])(e)
    private Expr visitInstanceof(X10Instanceof n) throws SemanticException {
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
        @SuppressWarnings("unchecked") // Casting to a generic type parameter
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
