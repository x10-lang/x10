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
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Throw;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.Here;
import x10.ast.Here_c;
import x10.ast.Next;
import x10.ast.Offer;
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
import x10.ast.X10LocalDecl_c;
import x10.ast.X10New;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.extension.X10Ext;
import x10.extension.X10Ext_c;
import x10.optimizations.ForLoopOptimizer;
import x10.types.AsyncInstance;
import x10.types.AtDef;
import x10.types.AtInstance;
import x10.types.ClosureDef;
import x10.types.ConstrainedType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.MethodInstance;
import x10.types.X10Def;
import x10.types.X10FieldInstance;
import x10.types.X10ParsedClassType;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constants.StringValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.util.AltSynthesizer;
import x10.util.AnnotationUtils;
import x10.util.ClosureSynthesizer;
import x10.util.Synthesizer;
import x10.util.synthesizer.InstanceCallSynth;
import x10.visit.Desugarer.Substitution;
import x10cuda.ast.CUDAKernel;

/**
 * Visitor to desugar the AST before code generation.
 * 
 * NOTE: all the nodes created in the Desugarer must have the appropriate type information.
 * The NodeFactory methods do not fill in the type information.  Use the helper methods available
 * in the Desugarer to create expressions, or see how the type information is filled in for other
 * types of nodes elsewhere in the Desugarer.  TODO: factor out the helper methods into the
 * {@link Synthesizer}.
 */
public class Lowerer extends ContextVisitor {
    private final Synthesizer synth;
    private final AltSynthesizer altsynth;
    private final boolean isManagedX10;
    
    public Lowerer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
        isManagedX10 = ((x10.ExtensionInfo) job.extensionInfo()).isManagedX10();
    }

    private int count;
    //Collecting Finish Use: store reducer
    private Stack<FinishExpr> reducerS = new Stack<FinishExpr>();
    private Stack<Local> clockStack = new Stack<Local>();
    private int flag = 0;

    private Name getTmp() {
        return Name.make("__lowerer__var__" + (count++) + "__");
    }

    private static final Name RUN_AT = Name.make("runAt");
    private static final Name EVAL_AT = Name.make("evalAt");
    private static final Name RUN_ASYNC = Name.make("runAsync");
    private static final Name RUN_UNCOUNTED_ASYNC = Name.make("runUncountedAsync");
    private static final Name RUN_IMMEDIATE_ASYNC = Name.make("runImmediateAsync");
    private static final Name HOME = Name.make("home");
    private static final Name HERE_INT = Name.make("hereInt");
    private static final Name NEXT = Name.make("advanceAll");
    private static final Name RESUME = Name.make("resumeAll");
    private static final Name DROP = Name.make("drop");
    private static final Name MAKE = Name.make("make");
    
    private static final Name AWAIT_ATOMIC = Name.make("awaitAtomic");
    private static final Name ENTER_ATOMIC = Name.make("enterAtomic");
    private static final Name ENSURE_NOT_IN_ATOMIC = Name.make("ensureNotInAtomic");
    private static final Name EXIT_ATOMIC = Name.make("exitAtomic");
    
    private static final Name START_FINISH = Name.make("startFinish");
    private static final Name PUSH_EXCEPTION = Name.make("pushException");
    private static final Name STOP_FINISH = Name.make("stopFinish");
    private static final Name PLACES = Name.make("places");
    private static final Name RESTRICTION = Name.make("restriction");
    private static final Name CONVERT = Converter.operator_as;
    private static final Name CONVERT_IMPLICITLY = Converter.implicit_operator_as;
    private static final Name DIST = Name.make("dist");
    private static final Name LOCAL_INDICES = Name.make("localIndices");
    private static final Name PLACE_GROUP = Name.make("placeGroup");
    
    private static final QName PRAGMA = QName.make("x10.compiler.Pragma");
    private static final QName REF = QName.make("x10.compiler.Ref");
    private static final QName UNCOUNTED = QName.make("x10.compiler.Uncounted");
    private static final QName IMMEDIATE = QName.make("x10.compiler.Immediate");
    private static final QName ASYNC_CLOSURE = QName.make("x10.compiler.AsyncClosure");
    private static final QName NAMED_MESSAGE = QName.make("x10.compiler.NamedMessage");
    
    private static final Name START_COLLECTING_FINISH = Name.make("startCollectingFinish");
    private static final Name STOP_COLLECTING_FINISH = Name.make("stopCollectingFinish");
    private static final Name OFFER = Name.make("makeOffer");  
    
    //added for scalable finish
    private static final Name START_LOCAL_FINISH = Name.make("startLocalFinish");
    private static final Name START_SIMPLE_FINISH = Name.make("startSimpleFinish");
    
    @Override
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
    		Context xc = context();
    		Position pos = finish.position();
    		Name name = xc.makeFreshName("clock");
    		Flags flags = Flags.FINAL;
    		Type type = ts.Clock();
    		
    		final Name varName = xc.getNewVarName();
			final LocalDef li = ts.localDef(pos, flags, Types.ref(type), varName);
			final Id varId = nf.Id(pos, varName);
			final Local ldRef = (Local) nf.Local(pos, varId).localInstance(li.asInstance()).type(type);
			clockStack.push(ldRef);
			
			final Name outerVarName = xc.getNewVarName();
			final LocalDef outerLi = ts.localDef(pos, flags, Types.ref(type), outerVarName);
			final Id outerVarId = nf.Id(pos, outerVarName);
			final Local outerLdRef = (Local) nf.Local(pos, outerVarId).localInstance(outerLi.asInstance()).type(type);

			try {
				Expr clock = synth.makeStaticCall(pos, type, MAKE, type, xc);
				final TypeNode tn = nf.CanonicalTypeNode(pos, type);
				Expr nullLit = nf.NullLit(pos).type(type);
				final LocalDecl outerLd = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.NONE), tn, outerVarId, nullLit).localDef(outerLi);
				
				Block block = synth.toBlock(finish.body());
				final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, flags), tn, varId, clock).localDef(li);
				Stmt assign = nf.Eval(pos, synth.makeAssign(pos, outerLdRef, Assign.ASSIGN, ldRef, xc));
				block = block.prepend(assign);
				block = block.prepend(ld);
				Eval drop = nf.Eval(pos, new InstanceCallSynth(nf, xc, pos, outerLdRef, DROP).genExpr());
	            Expr cond = nf.Binary(pos, outerLdRef, X10Binary_c.NE, nf.NullLit(pos).type(ts.Null())).type(ts.Boolean());
				Stmt stm1 = nf.Try(pos, block, Collections.<Catch>emptyList(), nf.Block(pos, nf.If(pos, cond, drop)));
				Node result = visitEdgeNoOverride(parent, nf.Block(pos, outerLd, nf.Finish(pos, stm1, false)));
				return result;
			} catch (SemanticException z) {
				return null;
			} finally {
			    clockStack.pop();
			}
    	}
    	
    	// match at (p) async S and treat it as if it were async (p) S.
    	if (n instanceof AtStmt) {
    	    AtStmt atStm = (AtStmt) n;
    	    Stmt body = atStm.body();
    	    Async async = toAsync(body);
    	    if (async==null)
    	        return null;
    	    Expr place = atStm.place();
    	    if (ts.hasSameClassDef(Types.baseType(place.type()), ts.GlobalRef())) {
    	        try {
    	            place = synth.makeFieldAccess(async.position(),place, ts.homeName(), context());
    	        } catch (SemanticException e) {
    	        }
    	    }
    	    List<Expr> clocks = async.clocks();
    	    place = (Expr) visitEdgeNoOverride(atStm, place);
    	    body = (Stmt) visitEdgeNoOverride(async, async.body());
    	    if (clocks != null && ! clocks.isEmpty()) {
    	        List<Expr> nclocks = new ArrayList<Expr>();
    	        for (Expr c : clocks) {
    	            nclocks.add((Expr) visitEdgeNoOverride(async, c));
    	        }
    	        clocks =nclocks;
    	    }
    	    try {
    	        Expr prof = getProfile(atStm.atDef());
    	        List<X10ClassType> annotations = AnnotationUtils.getAnnotations(n);
    	        if (annotations != null) {
    	            List<X10ClassType> bodyAnnotations = AnnotationUtils.getAnnotations(async);
    	            if (bodyAnnotations != null) {
    	                annotations.addAll(bodyAnnotations);
    	            }
    	        } else {
    	            annotations = AnnotationUtils.getAnnotations(async);;
    	        }
    	        return visitAsyncPlace(async, place, body, atStm.atDef().capturedEnvironment(), annotations, prof);
    	    } catch (SemanticException z) {
    	        return null;
    	    }
    	}

    	// CUDA KLUDGE: match async at(p) @CUDA S and compile it as if it were async(p) @CUDA S.
    	// TODO: Think about whether we can instead use a pattern that matches current (X10 2.4.3)
    	//       idioms for remote activity spawning.
    	if (n instanceof Async) {
    		Async async = (Async) n;
    		Stmt body = async.body();
    		AtStmt atStm = toAtStmt(body);
    		if (atStm==null) {
    			return null;
    		}
            if (!(atStm.body() instanceof CUDAKernel)) {
                return null;
            }
    		Expr place = atStm.place(); 
    		if (ts.hasSameClassDef(Types.baseType(place.type()), ts.GlobalRef())) {
    			try {
    				place = synth.makeFieldAccess(async.position(),place, ts.homeName(), context());
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
                Expr prof = getProfile(async.asyncDef());
                List<X10ClassType> annotations = AnnotationUtils.getAnnotations(n);
                if (annotations != null) {
                    List<X10ClassType> bodyAnnotations = AnnotationUtils.getAnnotations(async);
                    if (bodyAnnotations != null) {
                        annotations.addAll(bodyAnnotations);
                    }
                } else {
                    annotations = AnnotationUtils.getAnnotations(async);;
                }
    			return visitAsyncPlace(async, place, body, atStm.atDef().capturedEnvironment(), annotations, prof);
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
    @Override
    protected NodeVisitor enterCall(Node parent, Node n) {
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

        return this;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Async)
            return visitAsync(old, (Async) n);
        if (n instanceof AtStmt)
            return visitAtStmt((AtStmt) n);
        if (n instanceof AtExpr)
            return visitAtExpr((AtExpr) n);
        if (n instanceof Here_c)
            return visitHere((Here_c) n);
        if (n instanceof Next)
            return visitNext((Next) n);
        if (n instanceof Atomic)
            return visitAtomic((Atomic) n);
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
        if (n instanceof LocalDecl)
            return visitLocalDecl((LocalDecl) n);
        if (n instanceof Resume)
            return visitResume((Resume) n);
        return n;
    }

    /** Wrap the given statement in code to catch all exceptions and rethrow them wrapped to
     * avoid having a checked exception escape a closure.  This is necessary since in javac
     * we cannot generate code for a closure that throws a checked exception.  XRX code unwraps
     * them on the completion message, or puts them into the MultipleExceptions in their wrapped form.
     * @param useReturn whether or not to return a value of type returnType in the catch block
     * @author Dave Cunningham
     */
    private Block atExceptionWrap(Position pos, Stmt atBody, boolean useReturn, Type returnType) throws SemanticException {
    	TypeNode returnTypeNode = nf.CanonicalTypeNode(pos, returnType); 

    	// try { at_body }
        Block tryBlock = synth.toBlock(atBody);

        List<Catch> catches = new ArrayList<Catch>();
        // if return_type is null, return is omitted and T is Int, otherwise T is return_type 
        { // catch (e:CheckedThrowable) { /*return*/ Runtime.wrapAtChecked[T](e); }
	        Name catchFormalName = getTmp();
	        LocalDef catchFormalLocalDef = ts.localDef(pos, ts.NoFlags(), Types.ref(ts.CheckedThrowable()), catchFormalName);
	        Formal catchFormal = nf.Formal(pos, nf.FlagsNode(pos, ts.NoFlags()),
	                nf.CanonicalTypeNode(pos, ts.CheckedThrowable()), nf.Id(pos, catchFormalName)).localDef(catchFormalLocalDef);                
	        Expr catchLocal = nf.Local(pos, nf.Id(pos, catchFormalName)).localInstance(catchFormalLocalDef.asInstance()).type(ts.CheckedThrowable());
	        Expr wrap = synth.makeStaticCall(pos, ts.Runtime(), Name.make("wrapAtChecked"), Collections.singletonList(returnTypeNode), Collections.singletonList(catchLocal), ts.Exception(), context());

	        // [DC] managed backend weirdness... If i just return wrap here, i get errors due to missing $unbox call.  I have to put it in a local var and return the local var.
	        Name wrapStoreName = getTmp();
	        Id wrapStoreId = nf.Id(pos, wrapStoreName);
	        LocalDef wrapStoreLocalDef = ts.localDef(pos, ts.NoFlags(), Types.ref(returnType), wrapStoreName);
	        LocalDecl wrapStoreLocalDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.NoFlags()), returnTypeNode, wrapStoreId, wrap).localDef(wrapStoreLocalDef);
	        Expr wrapStoreLocal = nf.Local(pos, wrapStoreId).localInstance(wrapStoreLocalDef.asInstance()).type(returnType);
	        
	        Block catchBlock;
	        if (useReturn) {
	        	catchBlock= nf.Block(pos, wrapStoreLocalDecl, nf.Return(pos, wrapStoreLocal));
	        } else {
	        	catchBlock= nf.Block(pos, wrapStoreLocalDecl);
	        }
	        catches.add(nf.Catch(pos, catchFormal, catchBlock));
        }
	        
        Block closure_body = synth.toBlock(nf.Try(pos, tryBlock, catches, null));
        
        return closure_body;
    }

    private Expr visitAtExpr(AtExpr e) throws SemanticException {
		Position pos = e.position();
		Expr place = getPlace(pos, e.place());

		Position bPos = e.body().position();
		ClosureDef cDef = e.closureDef().position(bPos);
		
		// If in a clocked context, must capture implicit clock variable 
		// being added by the lowering phase.
		if (!clockStack.isEmpty()) {
		    cDef.addCapturedVariable(clockStack.peek().localInstance());
		}
		
		Block at_body = e.body();
		if (isManagedX10) {
		    at_body = atExceptionWrap(bPos, at_body, true, e.returnType().type());
		}

		if (AnnotationUtils.hasAnnotation(e, ts.RemoteInvocation())) {
		    throw new UnsupportedOperationException("Usage error: RemoteInvocation annotations not supported on at expressions");
		}
		
		Closure closure_ = nf.Closure(bPos,  e.formals(), e.guard(), e.returnType(), at_body);
		Expr closure = closure_.closureDef(cDef).type(cDef.classDef().asType());

		List<TypeNode> typeArgs = Arrays.asList(new TypeNode[] { e.returnType() });
		Expr prof = getProfile(e.closureDef()); 
		List<Expr> args = new ArrayList<Expr>(Arrays.asList(new Expr[] { place, closure, prof }));
		Expr result = synth.makeStaticCall(pos, ts.Runtime(), EVAL_AT,
				typeArgs, args, e.type(), context());

		// [DC] It seems this can sometimes be null, even though it ought not to be.
		// I suspect some pass before this one is not filling it in, in some new code.
		if (at_body.exceptions() != null) {
		    for (Type thrown : at_body.exceptions()) {
		    	TypeNode thrown_node = nf.UnknownTypeNode(pos).typeRef(Types.ref(thrown));
		        //XTENLANG-3086 change from ts.CheckedThrowable() to ts.Runtime()
				List<TypeNode> typeArgs2 = Arrays.asList(new TypeNode[] { thrown_node, e.returnType() });
		        result = synth.makeStaticCall(pos, ts.CheckedThrowable(), Name.make("pretendToThrow"), typeArgs2, Collections.<Expr>singletonList(result),  e.returnType().type(), context());
		    }
		}

		return result;
    }

    Expr getPlace(Position pos, Expr place) throws SemanticException{
        if (! ts.isImplicitCastValid(place.type(), ts.Place(), context())) {
            throw new InternalCompilerError("The place argument of an \"at\" is not of type Place", place.position());
        }
        return place;
    }

    private static CodeInstance<?> findEnclosingCode(CodeInstance<?> ci) {
        if (ci instanceof AsyncInstance) {
            return findEnclosingCode(((AsyncInstance) ci).methodContainer());
        } else if (ci instanceof AtInstance) {
            return findEnclosingCode(((AtInstance) ci).methodContainer());
        }
        return ci;
    }

    protected Expr getEndpoint(AtDef def) throws SemanticException {
        Type t = ts.Endpoint();
        List<Type> as = def.annotationsMatching(t);
        for (Type at : as) {
            // TODO: fail if more than 1 @Endpoint annotation
            return getEndpointExpr(at);
        }
        return null;
    }

    protected Expr getEndpointExpr(Type at) throws SemanticException {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (0 < act.propertyInitializers().size()) {
                return act.propertyInitializer(0);
            }
        }
        return null;
    }

    protected Expr getProfile(X10Def def) throws SemanticException {
        Type t = ts.Profile();
        List<Type> as = def.annotationsMatching(t);
        for (Type at : as) {
            // TODO: fail if more than 1 @Profile annotation
            return getProfileExpr(at);
        }
        return nodeFactory().NullLit(def.position()).type(typeSystem().Null());
    }

    protected Expr getProfileExpr(Type at) throws SemanticException {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (0 < act.propertyInitializers().size()) {
                return act.propertyInitializer(0);
            }
        }
        return nodeFactory().NullLit(at.position()).type(typeSystem().Null());
    }
    
    private Stmt visitAtStmt(AtStmt a) throws SemanticException {
        Position pos = a.position();

        // If in a clocked context, must capture implicit clock variable 
        // being added by the lowering phase.
        List<VarInstance<? extends VarDef>> env = a.atDef().capturedEnvironment();
        if (!clockStack.isEmpty()) {
            env = new ArrayList<VarInstance<? extends VarDef>>(env);
            env.add(clockStack.peek().localInstance());
        }

        
        Stmt at_body = a.body();
		Expr place = a.place();
		AtDef def = a.atDef();
        
        place = getPlace(pos, place);
		
		Block closure_body;
		if (isManagedX10) {
		    closure_body = atExceptionWrap(pos, at_body, false, ts.Int());
		} else {
		   closure_body = synth.toBlock(at_body);		    
		}
		List<X10ClassType> annotations = AnnotationUtils.getAnnotations(a);

		Closure closure = synth.makeClosure(at_body.position(), ts.Void(), closure_body, context(), annotations);
		closure.closureDef().setCapturedEnvironment(env);
		CodeInstance<?> mi = findEnclosingCode(Types.get(closure.closureDef().methodContainer()));
		closure.closureDef().setMethodContainer(Types.ref(mi));
		Expr[] args;
		Expr profile = getProfile(def);
		Expr endpoint = getEndpoint(def);
		if (null != endpoint) {
		    args = new Expr[] { place, closure, profile, endpoint };
		} else {
		    args = new Expr[] { place, closure, profile };
		}
		List<Stmt> statements = new ArrayList<Stmt>();
		Stmt run_at = nf.Eval(pos,
				synth.makeStaticCall(pos, ts.Runtime(), RUN_AT,
						Arrays.asList(args), ts.Void(),
						context()));
		statements.add(run_at);
		// [DC] It seems this can sometimes be null, even though it ought not to be.
		// I suspect some pass before this one is not filling it in, in some new code.
		if (at_body.exceptions() != null) {
		    for (Type thrown : at_body.exceptions()) {
		    	TypeNode thrown_node = nf.UnknownTypeNode(pos).typeRef(Types.ref(thrown));
		        //XTENLANG-3086 change from ts.CheckedThrowable() to ts.Runtime()
		        Expr pretendToThrow = synth.makeStaticCall(pos, ts.CheckedThrowable(), Name.make("pretendToThrow"), Collections.singletonList(thrown_node), Collections.<Expr>emptyList(),  ts.Void(), context());
		    	statements.add(nf.Eval(pos, pretendToThrow));
		    }
		}
		return nf.Block(pos, statements);
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
                    return (AtStmt) body;
                }
            }
        }
        return null;
    }

    private Async toAsync(Stmt body) {
    	if ((body instanceof Async)) {
    		return (Async) body;
    	}
    	if (body instanceof Block) {
    		Block block = (Block) body;
    		if (block.statements().size()==1) {
    			body = block.statements().get(0);
    			if ((body instanceof Async)) {
    				return (Async) body;
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
        List<X10ClassType> annotations = AnnotationUtils.getAnnotations(a);
        List<VarInstance<? extends VarDef>> env = a.asyncDef().capturedEnvironment();
        if (a.clocked()) {
            env = new ArrayList<VarInstance<? extends VarDef>>(env);
            env.add(clockStack.peek().localInstance());
        }
        if (isUncountedAsync(ts, a)) {
        	if (old instanceof Async) {
            	 return uncountedAsync(pos, a.body(), env, isImmediateAsync(ts, a), annotations);
        	}
        }

        return async(pos, a.body(), clocks, annotations, env);
    }
    // Begin asyncs
    // rewrite @Uncounted async S, with special translation for @Uncounted async at (p) S.
    private Stmt visitAsyncPlace(Async a, Expr place, Stmt body, List<VarInstance<? extends VarDef>> env, 
                                 List<X10ClassType> annotations, Expr prof) throws SemanticException {
        List<Expr> clocks = clocks(a.clocked(), a.clocks());
        Position pos = a.position();
        if (a.clocked()) {
            env = new ArrayList<VarInstance<? extends VarDef>>(env);
            env.add(clockStack.peek().localInstance());
        }
        if (isUncountedAsync(ts, a)) {
            return uncountedAsync(pos, body, place, env, prof, isImmediateAsync(ts, a), annotations);
        }

        return async(pos, body, clocks, place, annotations, env, prof);
    }

    public static boolean isUncountedAsync(TypeSystem ts, Async a) {
        return AnnotationUtils.hasAnnotation(ts, a, UNCOUNTED);
    }
    
    public static boolean isImmediateAsync(TypeSystem ts, Async a) {
        return AnnotationUtils.hasAnnotation(ts, a, IMMEDIATE);
    }

    private Stmt async(Position pos, Stmt body, List<Expr> clocks, Expr place, List<X10ClassType> annotations,
            List<VarInstance<? extends VarDef>> env, Expr prof) throws SemanticException {
        if (ts.isImplicitCastValid(place.type(), ts.GlobalRef(), context())) {
            place = synth.makeFieldAccess(pos,place, ts.homeName(), context());
        }
        if (clocks.size() == 0)
        	return async(pos, body, place, annotations, env, prof);
        Type clockRailType = ts.Rail(ts.Clock());
        Tuple clockRail = (Tuple) nf.Tuple(pos, clocks).type(clockRailType);

        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { place, clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { ts.Place(), clockRailType})),
                             body, annotations, env, prof);
    }

    private Stmt async(Position pos, Stmt body, Expr place, List<X10ClassType> annotations,
            List<VarInstance<? extends VarDef>> env, Expr prof) throws SemanticException {
        List<Expr> l = new ArrayList<Expr>(1);
        l.add(place);
        List<Type> t = new ArrayList<Type>(1);
        t.add(ts.Place());
        return makeAsyncBody(pos, l, t, body, annotations, env, prof);
    }

    private Stmt async(Position pos, Stmt body, List<Expr> clocks, List<X10ClassType> annotations,
            List<VarInstance<? extends VarDef>> env) throws SemanticException {
        if (clocks.size() == 0)
        	return async(pos, body, annotations, env);
        Type clockRailType = ts.Rail(ts.Clock());
        Tuple clockRail = (Tuple) nf.Tuple(pos, clocks).type(clockRailType);
        return makeAsyncBody(pos, new ArrayList<Expr>(Arrays.asList(new Expr[] { clockRail })),
                             new ArrayList<Type>(Arrays.asList(new Type[] { clockRailType})), body,
                             annotations, env, null);
    }

    private Stmt async(Position pos, Stmt body, List<X10ClassType> annotations,
            List<VarInstance<? extends VarDef>> env) throws SemanticException {
        return makeAsyncBody(pos, new LinkedList<Expr>(),
                new LinkedList<Type>(), body, annotations, env, null);
    }

    private Stmt makeAsyncBody(Position pos, List<Expr> exprs, List<Type> types,
            Stmt async_body, List<X10ClassType> annotations,
            List<VarInstance<? extends VarDef>> env, Expr prof) throws SemanticException {

    	if (annotations == null)
    		annotations = new ArrayList<X10ClassType>(1);
    	annotations.add((X10ClassType) ts.systemResolver().findOne(ASYNC_CLOSURE));

        Stmt closure_body = async_body;

        if (isManagedX10) {
            // A closure's apply method can't throw checked exceptions in Java.
            // So wrap in a try/catch and wrap & rethrow any checked exceptions that arise.

            // try { async_body }
            Block tryBlock = synth.toBlock(async_body);

            List<Catch> catches = new ArrayList<Catch>();
            { // catch (e:Error) { throw e; }
                Name catchFormalName = getTmp();
                LocalDef catchFormalLocalDef = ts.localDef(pos, ts.NoFlags(), Types.ref(ts.Error()), catchFormalName);
                Formal catchFormal = nf.Formal(pos, nf.FlagsNode(pos, ts.NoFlags()),
                        nf.CanonicalTypeNode(pos, ts.Error()), nf.Id(pos, catchFormalName)).localDef(catchFormalLocalDef);                
                Expr catchLocal = nf.Local(pos, nf.Id(pos, catchFormalName)).localInstance(catchFormalLocalDef.asInstance()).type(ts.Error());
                Block catchBlock = nf.Block(pos, nf.Throw(pos, catchLocal));
                catches.add(nf.Catch(pos, catchFormal, catchBlock));
            }
            { // catch (e:CheckedThrowable) { throw Exception.ensureException(e); }
                Name catchFormalName = getTmp();
                LocalDef catchFormalLocalDef = ts.localDef(pos, ts.NoFlags(), Types.ref(ts.CheckedThrowable()), catchFormalName);
                Formal catchFormal = nf.Formal(pos, nf.FlagsNode(pos, ts.NoFlags()),
                        nf.CanonicalTypeNode(pos, ts.CheckedThrowable()), nf.Id(pos, catchFormalName)).localDef(catchFormalLocalDef);                
                Expr catchLocal = nf.Local(pos, nf.Id(pos, catchFormalName)).localInstance(catchFormalLocalDef.asInstance()).type(ts.CheckedThrowable());
                Expr wrap = synth.makeStaticCall(pos, ts.Exception(), Name.make("ensureException"), Collections.singletonList(catchLocal), ts.Exception(), context());
                Block catchBlock = nf.Block(pos, nf.Throw(pos, wrap));
                catches.add(nf.Catch(pos, catchFormal, catchBlock));
            }
                
            closure_body = nf.Try(pos, tryBlock, catches, null);
        }

        Closure closure = synth.makeClosure(async_body.position(), ts.Void(), synth.toBlock(closure_body), context(), annotations);
        
        closure.closureDef().setCapturedEnvironment(env);
        CodeInstance<?> mi = findEnclosingCode(Types.get(closure.closureDef().methodContainer()));
        closure.closureDef().setMethodContainer(Types.ref(mi));
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        if (prof != null) { 
	        exprs.add(prof);
	        types.add(prof.type());
        }	
        Stmt result = nf.Eval(pos,
                synth.makeStaticCall(pos, ts.Runtime(), RUN_ASYNC, exprs,
                        ts.Void(), types, context()));
        return result;
    }

    private Stmt uncountedAsync(Position pos, Stmt body, Expr place,
            List<VarInstance<? extends VarDef>> env, Expr prof, boolean isImmediate,
            List<X10ClassType> annotations) throws SemanticException {
        List<Expr> l = new ArrayList<Expr>(1);
        l.add(place);
        List<Type> t = new ArrayList<Type>(1);
        t.add(ts.Place());
        return makeUncountedAsyncBody(pos, l, t, body, env, prof, isImmediate, annotations);
    }

    private Stmt uncountedAsync(Position pos, Stmt body,
            List<VarInstance<? extends VarDef>> env, boolean isImmediate, List<X10ClassType> annotations) throws SemanticException {
        return makeUncountedAsyncBody(pos, new LinkedList<Expr>(),
                new LinkedList<Type>(), body, env, null, isImmediate, annotations);
    }

    private Stmt makeUncountedAsyncBody(Position pos, List<Expr> exprs, List<Type> types, Stmt body,
            List<VarInstance<? extends VarDef>> env, Expr prof, boolean isImmediate, List<X10ClassType> annotations) throws SemanticException {
        Closure closure = synth.makeClosure(body.position(), ts.Void(), synth.toBlock(body), context(), annotations);
        closure.closureDef().setCapturedEnvironment(env);
        CodeInstance<?> mi = findEnclosingCode(Types.get(closure.closureDef().methodContainer()));
        closure.closureDef().setMethodContainer(Types.ref(mi));
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        if (prof != null) { 
            exprs.add(prof);
            types.add(prof.type());
        }	
        Stmt result = nf.Eval(pos,
                              synth.makeStaticCall(pos, ts.Runtime(), 
                                                   isImmediate ? RUN_IMMEDIATE_ASYNC : RUN_UNCOUNTED_ASYNC, 
                                                   exprs, ts.Void(), types, context()));
        return result;
    }
    // end Async


    // here -> Runtime.home()
    private Expr visitHere(Here_c h) throws SemanticException {
        Position pos = h.position();
        return call(pos, HOME, ts.Place());
    }

    // next; -> Clock.advanceAll(); (deprecated)
    private Stmt visitNext(Next n) throws SemanticException {
        Position pos = n.position();
        return nf.Eval(pos, synth.makeStaticCall(pos, ts.Clock(), NEXT, ts.Void(), context()));
    }
    
    // resume; -> Clock.resumeAll(); (deprecated)
    private Stmt visitResume(Resume n) throws SemanticException {
        Position pos = n.position();
        return nf.Eval(pos, synth.makeStaticCall(pos, ts.Clock(), RESUME, ts.Void(), context()));
    }

    // atomic S; -> try { Runtime.enterAtomic(); S } finally { Runtime.exitAtomic(); }
    private Stmt visitAtomic(Atomic a) throws SemanticException {
        Position pos = a.position();
        Block tryBlock = nf.Block(pos, nf.Eval(pos, call(pos, ENTER_ATOMIC, ts.Void())), a.body());
        Block finallyBlock = nf.Block(pos, nf.Eval(pos, call(pos, EXIT_ATOMIC, ts.Void())));
        return nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    }

    private Stmt wrap(Position pos, Stmt s) {
        return s.reachable() ? nf.Block(pos, s, nf.Break(pos)) : s;
    }

    protected Expr getLiteral(Position pos, Type type, boolean val) {
        type = Types.baseType(type);
        if (ts.isBoolean(type)) {
            Type t = ts.Boolean();
            t = Types.addSelfBinding(t, val ? ts.TRUE() : ts.FALSE());
            return nf.BooleanLit(pos, val).type(t);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
    }

    // when(E1) S1 or(E2) S2...; ->
    //    Runtime.ensureNotInAtomic();
    //    try { Runtime.enterAtomic();
    //          while (true) { if (E1) { S1; break; } if (E2) { S2; break; } ... Runtime.awaitAtomic(); }
    //    finally { Runtime.exitAtomic(); }
    private Stmt visitWhen(When w) throws SemanticException {
        Position pos = w.position();
        Block body = nf.Block(pos, nf.If(pos, w.expr(), wrap(pos, w.stmt())));
        for(int i=0; i<w.stmts().size(); i++) {
            body = body.append(nf.If(pos, (Expr) w.exprs().get(i), wrap(pos, (Stmt) w.stmts().get(i))));
        }
        body = body.append(nf.Eval(pos, call(pos, AWAIT_ATOMIC, ts.Void())));
        Block tryBlock = nf.Block(pos, 
        		nf.Eval(pos, call(pos, ENTER_ATOMIC, ts.Void())),
        		nf.While(pos, getLiteral(pos, ts.Boolean(), true), body));
        Block finallyBlock = nf.Block(pos, nf.Eval(pos, call(pos, EXIT_ATOMIC, ts.Void())));
        return nf.Block(pos, 
        		nf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, ts.Void())),
        		nf.Try(pos, 
        				tryBlock, Collections.<Catch>emptyList(), 
        				finallyBlock));
    }

    protected Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, ts.Runtime(), name, returnType, context());
    }

    protected Expr call(Position pos, Name name, Expr arg, Type returnType) throws SemanticException {
        return synth.makeStaticCall(pos, ts.Runtime(), name, Collections.singletonList(arg), returnType, context());
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
        Type annotation = ts.systemResolver().findOne(QName.make("x10.compiler.FinishAsync"));
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
						reporter.report(0,"WARNING:compiler inferes different annotations from what the programer sets in "+job.source().name());
						if(reporter.should_report("verbose", 1)){
							reporter.report(5,"\tcompiler inferes "+p1);
							reporter.report(5,"\tprogrammer annotates "+p2);
						}
					}
				}
				a = allannots.get(allannots.size()-1);
				if(reporter.should_report("", 1)) 
					reporter.report(1,a.toString());
				p = getPatternFromAnnotation(a);
				
        	}else{
        		reporter.report(0,"annotation is not correct "+ allannots.size());
        	}
        }
        Type atype = ts.systemResolver().findOne(PRAGMA);
        List<X10ClassType> atypes  = ((X10Ext) f.ext()).annotationMatching(atype);
        if (!atypes.isEmpty()) {
            return call(pos, START_FINISH, atypes.get(0).propertyInitializer(0), ts.FinishState());
        }
        
        switch(p){
        case 1:return call(pos, START_LOCAL_FINISH, ts.FinishState());
        case 2:return call(pos, START_SIMPLE_FINISH, ts.FinishState());
        //TODO:more patterns can be filled here
        default:return call(pos, START_FINISH, ts.FinishState());
        }
    }

    // finish S; ->
    // Native X10:
    //    {
    //    Runtime.ensureNotInAtomic();
    //    val fresh = Runtime.startFinish();
    //    try { S; }
    //    catch (t:CheckedThrowable) { Runtime.pushException(t); }
    //    Runtime.stopFinish(fresh);
    //    }
    // Managed X10:
    //    {
    //    Runtime.ensureNotInAtomic();
    //    val fresh = Runtime.startFinish();
    //    try { S; }
    //    catch (t:CheckedThrowable) { Runtime.pushException(t); throw new RuntimeException)(); }
    //    finally { Runtime.stopFinish(fresh); }
    //    }
    private Stmt visitFinish(Finish f) throws SemanticException {
        Position pos = f.position();
        Name tmp = Name.makeFresh("ct");
        
        // TODO: merge with the call() function
        Type catchType = ts.CheckedThrowable();
        MethodInstance mi = ts.findMethod(ts.Runtime(),
            ts.MethodMatcher(ts.Runtime(), PUSH_EXCEPTION, Collections.singletonList(catchType), context()));
        LocalDef lDef = ts.localDef(pos, ts.NoFlags(), Types.ref(catchType), tmp);
        Formal formal = nf.Formal(pos, nf.FlagsNode(pos, ts.NoFlags()),
            nf.CanonicalTypeNode(pos, catchType), nf.Id(pos, tmp)).localDef(lDef);
        Expr local = nf.Local(pos, nf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(catchType);
        Expr peCall = nf.X10Call(pos, nf.CanonicalTypeNode(pos, ts.Runtime()),
                nf.Id(pos, PUSH_EXCEPTION), Collections.<TypeNode>emptyList(),
                Collections.singletonList(local)).methodInstance(mi).type(ts.Void());
        Expr startCall = specializedFinish2(f);

        Context xc = context();
        final Name varName = Name.makeFresh("fs");
        final Type type = ts.FinishState();
        final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(type), varName);
        final Id varId = nf.Id(pos, varName);
        final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, type), varId, startCall).localDef(li).type(nf.CanonicalTypeNode(pos, type));
        final Local ldRef = (Local) nf.Local(pos, varId).localInstance(li.asInstance()).type(type);

        Block tryBlock = nf.Block(pos, f.body());
        Catch catchBlock;
        if (isManagedX10) {
            // Need to confuse Java's definite assignment analysis so it won't complain...
            Type re = ts.Exception();
            X10ConstructorInstance ci = ts.findConstructor(re, ts.ConstructorMatcher(re, Collections.<Type>emptyList(), context()));
            Expr newRE = nf.New(pos, nf.CanonicalTypeNode(pos, re), Collections.<Expr>emptyList()).constructorInstance(ci).type(re);
            catchBlock = nf.Catch(pos, formal, nf.Block(pos, nf.Eval(pos, peCall), nf.Throw(pos, newRE)));
        } else {
            catchBlock = nf.Catch(pos, formal, nf.Block(pos, nf.Eval(pos, peCall)));            
        }
        Stmt endCall = nf.Eval(pos, call(pos, STOP_FINISH, ldRef, ts.Void()));

        // For ManagedX10, we generate as a try/catch/finally block as the simplest way to
        // deal with definite assignment checking and ensuring a well-defined block
        // to generate async initialization code.
        // For NativeX10, we do not need the finally block, so use a simpler try/catch
        // followed by the stopFinish.
        if (isManagedX10) {
            Try tcfBlock = nf.Try(pos, tryBlock, Collections.singletonList(catchBlock), nf.Block(pos, endCall));
            
            X10Ext_c ext = (X10Ext_c) f.ext();
            if (ext.initVals != null) {
                tcfBlock = (Try)((X10Ext_c)tcfBlock.ext()).asyncInitVal(ext.initVals);
            }
            
            return nf.Block(pos,
                            nf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, ts.Void())),
                            ld,
                            tcfBlock);
        } else {
            Try tcBlock = nf.Try(pos, tryBlock, Collections.singletonList(catchBlock));
            
            X10Ext_c ext = (X10Ext_c) f.ext();
            if (ext.initVals != null) {
                tcBlock = (Try)((X10Ext_c)tcBlock.ext()).asyncInitVal(ext.initVals);
            }
            
            return nf.Block(pos,
                            nf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, ts.Void())),
                            ld,
                            tcBlock,
                            endCall);
        }
    }

    // x = finish (R) S; ->
    // Native X10:
    //    {
    //    val fresh = Runtime.startCollectingFinish(R);
    //    try { S; }
    //    catch (t:CheckedThrowable) { Runtime.pushException(t); }
    //    x = Runtime.stopCollectingFinish(fresh);
    //    }
    // Managed X10:
    //    {
    //    val fresh = Runtime.startCollectingFinish(R);
    //    try { S; }
    //    catch (t:CheckedThrowable) { Runtime.pushException(t); throw new RuntimeException() }
    //    finally { x = Runtime.stopCollectingFinish(fresh); }
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
    		reducerType = Types.baseType(Types.get(ct.baseType()));
        }

        // reducerType is "Reducible[T]", and reducerTarget is "T"
        // Parse out T
        Type reducerTarget = Types.reducerType(reducerType);
        assert reducerTarget!=null;
        
        Call myCall = synth.makeStaticCall(pos, ts.Runtime(), START_COLLECTING_FINISH, Collections.<TypeNode>singletonList(nf.CanonicalTypeNode(pos, reducerTarget)), Collections.singletonList(reducer), ts.Void(), Collections.singletonList(reducerType), context());

        Context xc = context();
        final Name varName = Name.makeFresh("fs");
        final Type type = ts.FinishState();
        final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(type), varName);
        final Id varId = nf.Id(pos, varName);
        final LocalDecl s1 = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, type), varId, myCall).localDef(li).type(nf.CanonicalTypeNode(pos, type));
        final Local ldRef = (Local) nf.Local(pos, varId).localInstance(li.asInstance()).type(type);

        Block tryBlock = nf.Block(pos,f.body());

        // Begin catch block
        Name tmp2 = getTmp();
        Type catchType = ts.CheckedThrowable();
        MethodInstance mi = ts.findMethod(ts.Runtime(),
            ts.MethodMatcher(ts.Runtime(), PUSH_EXCEPTION, Collections.singletonList(catchType), context()));
        LocalDef lDef = ts.localDef(pos, ts.NoFlags(), Types.ref(catchType), tmp2);
        Formal formal = nf.Formal(pos, nf.FlagsNode(pos, ts.NoFlags()),
            nf.CanonicalTypeNode(pos, catchType), nf.Id(pos, tmp2)).localDef(lDef);
        Expr local = nf.Local(pos, nf.Id(pos, tmp2)).localInstance(lDef.asInstance()).type(catchType);
        Expr call = nf.X10Call(pos, nf.CanonicalTypeNode(pos, ts.Runtime()),
                nf.Id(pos, PUSH_EXCEPTION), Collections.<TypeNode>emptyList(),
                Collections.singletonList(local)).methodInstance(mi).type(ts.Void());
        Catch catchBlock;
        if (isManagedX10) {
            // Need to confuse Java's definite assignment analysis so it won't complain...
            Type re = ts.Exception();
            X10ConstructorInstance ci = ts.findConstructor(re, ts.ConstructorMatcher(re, Collections.<Type>emptyList(), context()));
            Expr newRE = nf.New(pos, nf.CanonicalTypeNode(pos, re), Collections.<Expr>emptyList()).constructorInstance(ci).type(re);
            catchBlock = nf.Catch(pos, formal, nf.Block(pos, nf.Eval(pos, call), nf.Throw(pos, newRE)));
        } else {
            catchBlock = nf.Catch(pos, formal, nf.Block(pos, nf.Eval(pos, call)));            
        }
        
        // Begin stopCollectingFinish stmt
        Stmt returnS = null;
        Call staticCall = synth.makeStaticCall(pos, ts.Runtime(), STOP_COLLECTING_FINISH, Collections.<TypeNode>singletonList(nf.CanonicalTypeNode(pos, reducerTarget)), Collections.<Expr>singletonList(ldRef), reducerTarget, Collections.<Type>singletonList(type), context());
        if ((l==null) && (n!=null)&& (r==null)) {
        	Expr left = n.left().type(reducerTarget);
            Expr b = synth.makeAssign(pos, left, Assign.ASSIGN, staticCall, xc);
            returnS = nf.Eval(pos, b);
        }
        if ((n==null) && (l!=null) && (r==null)) {
            Expr local2 = nf.Local(pos, l.name()).localInstance(l.localDef().asInstance()).type(reducerTarget);
         	Expr b = synth.makeAssign(pos, local2, Assign.ASSIGN, staticCall, xc);
            returnS = nf.Eval(pos, b);
        }
        if ((n==null) && (l==null) && (r!=null)) {
            returnS = nf.X10Return(pos, staticCall, true);
        }
        
        if (reducerS.size()>0) reducerS.pop();
        if (isManagedX10) {
            return nf.Block(pos, s1, nf.Try(pos, tryBlock, Collections.singletonList(catchBlock), nf.Block(pos, returnS)));            
        } else {
            return nf.Block(pos, s1, nf.Try(pos, tryBlock, Collections.singletonList(catchBlock)), returnS);            
        }
    }

    //  offer e ->
    //  x10.xrx.Runtime.offer(e);      
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
                reducerType = Types.baseType(Types.get(ct.baseType()));
            }
            X10ParsedClassType reducerTypeWithGenericType = null;
            Type thisType = reducerType;
            while(thisType != null) {
            //First check the reducerType itself is a reducible or not;
            //If not, it should be a class that implements reducible            
            if(ts.isReducible(((ClassType)thisType).def().asType())){
                //generic type case
                reducerTypeWithGenericType = (X10ParsedClassType) thisType;
                break;
            }
            else{ 
                //implement interface case
                for (Type t : ts.interfaces(thisType)) {
                	t = Types.baseType(t);
                    ClassType baseType = ((ClassType) t).def().asType();
                    if(ts.isReducible(baseType)){
                        reducerTypeWithGenericType = (X10ParsedClassType) t;
                        break;
                    }
                }
            }
            thisType = ts.superClass(thisType);
            }
            
            assert(reducerTypeWithGenericType != null);
            //because Reducible type only has one argument, we could take it directly
            expectType = reducerTypeWithGenericType.typeArguments().get(0);
        }
        else {
            expectType = offerTarget.type();
        }
   	 
        CanonicalTypeNode CCE = nf.CanonicalTypeNode(pos, expectType);
        TypeNode reducerA = (TypeNode) CCE;
        Expr newOfferTarget = nf.X10Cast(pos, reducerA, offerTarget,Converter.ConversionType.CHECKED).type(reducerA.type());

    	Call call = synth.makeStaticCall(pos, ts.Runtime(), OFFER, Collections.singletonList(offerTarget), ts.Void(), Collections.singletonList(expectType),  context());
    	
    	Stmt offercall = nf.Eval(pos, call);     	
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
            return nf.StmtSeq(pos, sList);
        }
      	return n;
    }

    // ateach (pt in D) S; ->
    //    { Runtime.ensureNotInAtomic(); val d = D.dist; for (p in d.places()) async (p) for (pt in d|here) async S; }
    // or 
    //    { Runtime.ensureNotInAtomic(); val d = D.pg; for (p in d.places()) async (p) for (pt in D.localIndices()) async S; }
    private Stmt visitAtEach(AtEach a) throws SemanticException {
        Position pos = a.position();
        Position bpos = a.body().position();
        Name tmp = getTmp();

        Expr domain = a.domain();
        Type dType = domain.type();
        if (ts.isX10RegionDistArray(dType)) {
            domain = altsynth.createFieldRef(pos, domain, DIST);
            dType = domain.type();
        }
  
        LocalDef lDef = ts.localDef(pos, ts.Final(), Types.ref(dType), tmp);
        LocalDecl local = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                                       nf.CanonicalTypeNode(pos, dType), nf.Id(pos, tmp), domain).localDef(lDef);
        X10Formal formal = (X10Formal) a.formal();
        Type fType = formal.type().type();
        assert (ts.isPoint(fType));
        
        // Construct inner forloop
        Stmt inner;
        List<VarInstance<? extends VarDef>> env = a.atDef().capturedEnvironment();
        if (ts.isDistArray(dType)) {
            MethodInstance rmi = ts.findMethod(dType, ts.MethodMatcher(dType, LOCAL_INDICES, Collections.EMPTY_LIST, context));
            Expr indices = altsynth.createInstanceCall(pos, altsynth.createLocal(pos, local), rmi);
            
            Stmt body = async(a.body().position(), a.body(), a.clocks(), null, env);
            inner = nf.ForLoop(pos, formal, indices, body).locals(formal.explode(this));
        } else {
            MethodInstance rmi = ts.findMethod(dType,
                                               ts.MethodMatcher(dType, RESTRICTION, Collections.singletonList((Type)ts.Place()), context()));
            Expr here = visitHere(nf.Here(bpos));
            Expr dAtPlace = nf.Call(bpos,
                                    nf.Local(pos, nf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                                    nf.Id(bpos, RESTRICTION),
                                    here).methodInstance(rmi).type(rmi.returnType());
            Expr here1 = visitHere(nf.Here(bpos));
            Stmt body = async(a.body().position(), a.body(), a.clocks(), here1, null, env, nf.NullLit(bpos).type(ts.Null()));
            inner = nf.ForLoop(pos, formal, dAtPlace, body).locals(formal.explode(this));
        }
        
        // Construct outer forloop and adjust captured environments
        Expr places;
        if (ts.isDistArray(dType)) {
            places = altsynth.createInstanceCall(pos, altsynth.createLocal(pos, local), PLACE_GROUP, context);
            
        } else {
            MethodInstance pmi = ts.findMethod(dType,
                                               ts.MethodMatcher(dType, PLACES, Collections.<Type>emptyList(), context()));
            places = nf.Call(bpos,
                             nf.Local(pos, nf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                             nf.Id(bpos, PLACES)).methodInstance(pmi).type(pmi.returnType());
        }
        Name pTmp = getTmp();
        LocalDef pDef = ts.localDef(pos, ts.Final(), Types.ref(ts.Place()), pTmp);
        Formal pFormal = nf.Formal(pos, nf.FlagsNode(pos, ts.Final()),
                                   nf.CanonicalTypeNode(pos, ts.Place()), nf.Id(pos, pTmp)).localDef(pDef);
        List<VarInstance<? extends VarDef>> env1 = new ArrayList<VarInstance<? extends VarDef>>(env);
        removeLocalInstance(env1, formal.localDef().asInstance());
        for (int i = 0; i < formal.localInstances().length; i++) {
            removeLocalInstance(env1, formal.localInstances()[i].asInstance());
        }
        env1.add(lDef.asInstance());
        Stmt body1 = async(bpos, inner, a.clocks(),
                           nf.Local(bpos, nf.Id(bpos, pTmp)).localInstance(pDef.asInstance()).type(ts.Place()),
                           null, env1, nf.NullLit(bpos).type(ts.Null()));
        Stmt outer = nf.ForLoop(pos, pFormal, places, body1);

        ForLoopOptimizer flo = new ForLoopOptimizer(job, ts, nf);
        For newLoop = (For)outer.visit(((ContextVisitor)flo.begin()).context(context()));
        return nf.Block(pos, 
                        nf.Eval(pos, call(pos, ENSURE_NOT_IN_ATOMIC, ts.Void())),
                        local, 
                        newLoop);
    }

    private boolean removeLocalInstance(List<VarInstance<? extends VarDef>> env, VarInstance<? extends VarDef> li) {
        VarInstance<? extends VarDef> match = null;
        for (VarInstance<? extends VarDef> vi : env) {
            if (vi.def() == li.def())
                match = vi;
        }
        if (match == null)
            return false;
        return env.remove(match);
    }

    private Stmt visitEval(Eval n) throws SemanticException {
        Position pos = n.position();
        if ((n.expr() instanceof Assign)&&(flag==1)) {
            Assign f = (Assign) n.expr();
            Expr right = f.right();
            if (right instanceof FinishExpr)
                return visitFinishExpr(f, null,null);
        }
        return n;
    }
}
