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

package x10.optimizations;

import static x10cpp.visit.ASTQuery.annotationNamed;
import static x10cpp.visit.ASTQuery.assertNumberOfInitializers;
import static x10cpp.visit.ASTQuery.getPropertyInit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FlagsNode;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.util.Synthesizer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ForLoop;
import x10.ast.Tuple;
import x10.ast.X10Binary_c;
import x10.ast.X10Formal;

import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.MethodInstance;
import x10.types.TypeParamSubst;

import polyglot.types.TypeSystem;
import x10.types.constants.IntegralValue;
import x10.types.constraints.CConstraint;
import x10.visit.Desugarer;
import x10.visit.NodeTransformingVisitor;
import x10.visit.Reinstantiator;
import x10.visit.TypeTransformer;

/**
 * Unrolls loops annotated with @Unroll(n).
 * Adapted from the X10DT refactoring engine.
 * 
 * @author rmfuhrer
 * @author igor
 */
public class LoopUnroller extends ContextVisitor {
    private static final String LOOP_UNROLL_TRANSFORMATION_NAME= "Loop Unroll";
    private static final String UNROLL_ANNOTATION_NAME= "x10.compiler.Unroll";

    private enum LoopLeftoverHandling {
        GENERATE_ASSERT,
        GENERATE_TAIL_LOOP
    }

    private class LoopParams {
        final VarDecl fLoopVar;
        final Expr fLoopDomain;
        Set<Expr> fLoopDomainValues= CollectionFactory.newHashSet();
        boolean fExtentUnknown;
        int fMin;
        Expr fMinSymbolic;
        int fMax;
        Expr fMaxSymbolic;
        int fStride;

        public LoopParams(VarDecl vd, Expr domain) {
            fLoopVar= vd;
            fLoopDomain= domain;
            fExtentUnknown= true;
        }

        public LoopParams(VarDecl vd, Expr domain, int min, int max, int stride) {
            this(vd, domain);
            fMin= min;
            fMax= max;
            fStride= stride;
            fExtentUnknown= false;
        }

        protected void addDomainValue(Expr e) {
            fLoopDomainValues.add(e);
        }

        protected int getMin() {
            return fMin;
        }

        protected Expr getMinSymbolic() {
            return fMinSymbolic;
        }

        protected void setMin(int min, Expr minSymbolic) {
            fMin= min;
            fMinSymbolic= minSymbolic;
            fExtentUnknown= false;
        }

        protected int getMax() {
            return fMax;
        }

        protected Expr getMaxSymbolic() {
            return fMaxSymbolic;
        }

        protected void setMax(int max, Expr maxSymbolic) {
            fMax= max;
            fMaxSymbolic= maxSymbolic;
            fExtentUnknown= false;
        }

        protected int getStride() {
            return fStride;
        }

        protected void setStride(int stride) {
            fStride= stride;
        }
    }

    /**
     * The user-selected ForLoop (often the same as fNode, if the latter is a ForLoop)
     */
    private ForLoop fLoop;

    /**
     * the user-supplied number of times to unroll the loop
     */
    private int fUnrollFactor;

    /**
     * records symbolic and constant information about the loop iteration domain
     */
    private LoopParams fLoopParams;

    /**
     * determines how to handle trailing iterations that are left over by the unrolled loop
     */
    private LoopLeftoverHandling fHandleLoopLeftovers;

    private final TypeSystem xts;

    private final NodeFactory xnf;

    public LoopUnroller(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }

    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ForLoop)
            return unrollLoop((ForLoop) n);
        return super.leaveCall(old, n, v);
    }

    public String getName() {
        return LOOP_UNROLL_TRANSFORMATION_NAME;
    }

    public void warn(String message) {
        System.err.println(getName()+": "+message);
        // TODO: put a WARNING on the compiler's error queue
    }

    public boolean fatalStatus(String message) {
        warn(message);
        return false;
    }

    public boolean okStatus() {
        return true;
    }

    private Type getAnnotation(Node o, String name) {
        try {
            return annotationNamed(xts, o, name);
        }
        catch (SemanticException e) {}
        return null;
    }

    private boolean extractUnrollFactor() {
        Type at = getAnnotation(fLoop, UNROLL_ANNOTATION_NAME);
        if (at == null)
            return false;
        assertNumberOfInitializers(at, 1);
        Object val = getPropertyInit(at, 0);
        assert (val instanceof Integer);
        fUnrollFactor = ((Integer) val).intValue();
        return okStatus();
    }

    public boolean checkPreconditions(ForLoop fLoop) {
        this.fLoop = fLoop;
        return checkInitialConditions() && checkFinalConditions();
    }

    public boolean checkInitialConditions() {
        boolean status= extractUnrollFactor();
        if (!status)
            return false;
        fHandleLoopLeftovers = LoopLeftoverHandling.GENERATE_TAIL_LOOP;
        return true;
    }

    public boolean checkFinalConditions() {
        try {
            boolean status= findLoopParams();
            if (status) {
                status= checkForInductionVarRefs();
            }
            return status;
        } catch (Exception e) {
            return fatalStatus(e.getMessage());
        }
    }

    private boolean checkForInductionVarRefs() {
        final boolean hasRefs[] = { false };
        fLoop.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof Local) {
                    Local local = (Local) n;
                    if (local.localInstance().equals(fLoop.formal().localDef().asInstance())) {
                        hasRefs[0] = true;
                    }
                }
                return super.enter(n);
            }
        });
        boolean ref_point = hasRefs[0] && ts.isPoint(fLoop.formal().type().type());
        return ref_point ? fatalStatus("Loop body has references to the Point loop induction variable") : okStatus();
    }

    private boolean findLoopParams() {
        fLoopParams= new LoopParams(fLoop.formal(), fLoop.domain());

        X10Formal loopVar = (X10Formal) fLoop.formal();
        List<Formal> explodedVars= loopVar.vars();

        if (explodedVars.size() > 1) {
            return fatalStatus("Exploded variable syntax is not yet supported for multi-dimensional loop iteration domains");
        }

        // use type of domain to check its rank
        if (!checkDomainIs1D(fLoop.domain())) {
            return fatalStatus("(at " + fLoopParams.fLoopVar.position() + ": cannot statically confirm that loop iteration domain is 1-dimensional");
        }

        // [DC] I think this no-longer applies:
        /*
        // now find the values that flow into the domain expr
        boolean status= findDomainValues();

        if (!status) {
            return status;
        }
        */

        // now examine the domain values to determine the loop bounds
        return extractDomainBounds();
    }

    private boolean extractDomainBounds() {
    	// [DC] don't understand fLoopDomainValues, maybe redundant?
    	/*
        if (fLoopParams.fLoopDomainValues.size() != 1) {
            return fatalStatus("Can only analyze loop with 1 possible iteration domain value (this one has "+fLoopParams.fLoopDomainValues.size()+")");
        }
        Expr v= fLoopParams.fLoopDomainValues.iterator().next();
*/
    	Expr v= fLoopParams.fLoopDomain;
    	
        if (v instanceof Call) {
            Call call= (Call) v;
            MethodInstance mi= call.methodInstance();
            List<Expr> args= call.arguments();
            if (isRegionConvertCall(mi)) {
                int dimen= args.size();
                if (dimen > 1) {
                    return fatalStatus("Cannot unroll loops over multi-dimensional iteration domains.");
                }
                Expr regionTuple= args.get(0);
                List<Expr> dimensionArgs= ((Tuple) regionTuple).arguments();
                Expr low, hi;
                if (dimensionArgs.get(0) instanceof Call) {
                    Call c= (Call) dimensionArgs.get(0);

                    MethodInstance cmi = c.methodInstance();
                    if (cmi.container().isInt() && cmi.name().equals(X10Binary_c.binaryMethodName(X10Binary_c.DOT_DOT))) {
                        low= c.arguments().get(0);
                        hi= c.arguments().get(1);
                    } else {
                        low= null;
                        hi= null;
                    }
                } else {
                    low= null;
                    hi= null;
                }
                if (low != null && hi != null) {
                    fLoopParams.fMin= getConstantValueOf(low);
                    fLoopParams.fMinSymbolic= low;
                    fLoopParams.fMax= getConstantValueOf(hi);
                    fLoopParams.fMaxSymbolic= hi;
                    fLoopParams.fStride= 1;
                }
            } else if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.array.Region") &&
                       mi.name().toString().equals("makeRectangular")) {
                Expr low, hi;
                low= args.get(0);
                hi= args.get(1);
                fLoopParams.fMin= getConstantValueOf(low);
                fLoopParams.fMinSymbolic= low;
                fLoopParams.fMax= getConstantValueOf(hi);
                fLoopParams.fMaxSymbolic= hi;
                fLoopParams.fStride= 1;
            } else if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Rail") &&
                    mi.name().toString().equals("makeVar")) {
                Expr size= args.get(0);
                fLoopParams.fMin= 1;
                fLoopParams.fMinSymbolic= intLit(1);
                fLoopParams.fMax= getConstantValueOf(size);
                fLoopParams.fMaxSymbolic= size;
                fLoopParams.fStride= 1;
            } else if (mi.container().isInt() && mi.name().equals(X10Binary_c.binaryMethodName(X10Binary_c.DOT_DOT))) {
                Expr low, hi;
                low= args.get(0);
                hi= args.get(1);
                fLoopParams.fMin= getConstantValueOf(low);
                fLoopParams.fMinSymbolic= low;
                fLoopParams.fMax= getConstantValueOf(hi);
                fLoopParams.fMaxSymbolic= hi;
                fLoopParams.fStride= 1;
            } else {
        		return fatalStatus("Don't understand iteration domain: " + call);
            }
        } else {
            return fatalStatus("Canont recognise loop domain for unrolling: " + fLoopParams.fLoopDomain);
        }
        return okStatus();
    }

    private boolean isRegionConvertCall(MethodInstance mi) {
        return mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.array.Region") &&
                mi.name().toString().equals("$convert");
    }

    private int getConstantValueOf(Expr e) {
        // TODO Need to produce the literal value here, but elsewhere need the symbolic value (e.g. when rewriting the region bounds)
        if (e.constantValue() != null && e.constantValue() instanceof IntegralValue) {
            return ((IntegralValue) e.constantValue()).intValue();
        }
        return -1; // TOTALLY BOGUS - probably need to return a RefactoringStatus to abort the refactoring
    }

    private boolean findDomainValues() {
        Local domainLocal;
        Expr domain= fLoop.domain();

        if (domain instanceof Field || domain instanceof Local) {
            if (domain instanceof Field) {
                Field f= (Field) domain;
                if (f.name().toString().equals("dist")) {
                    Receiver rcvr= f.target();
                    if (rcvr instanceof Local) {
                        domainLocal= ((Local) rcvr);
                    } else {
                        return fatalStatus("for-loop domain is a '.dist' pseudo-field access via something other than a local variable");
                    }
                } else {
                    return fatalStatus("for-loop domain is a field access to something other than '.dist'");
                }
            } else if (domain instanceof Local) {
                domainLocal= (Local) domain;
            } else {
                return fatalStatus("for-loop domain is neither a local var nor a 'dist' field access");
            }
//            VarDef localDef= domainLocal.localInstance().def();
//            Set<Term> domainTerms= fValueMap.getValues(localDef);
//
//            for(Term t: domainTerms) {
//                if (t instanceof Expr) {
//                    fLoopParams.addDomainValue((Expr) t);
//                } else {
//                    warn("Don't know what to do with domain term: " + t);
//                }
//            }
        } else if (domain instanceof Call) {
            fLoopParams.addDomainValue(domain);
        }
        return okStatus();
    }

    private boolean checkDomainIs1D(Expr domain) {
        ConstrainedType type= (ConstrainedType) domain.type();
        if (type.isRank(typeSystem().ONE(), context))
    		return true;
        else if (ts.isIntRange(type))
        	return true;
        return false;
     
    }

    private boolean constraintEq1(XTerm term) {
        XEquals eq= (XEquals) term;
        XTerm[] args= eq.arguments();
        XTerm left= args[0];
        XTerm right= args[1];

        if (right instanceof XLit) {
            XLit lit= (XLit) right;
            Object litVal= lit.val();
            return (litVal instanceof Number) && ((Number)litVal).intValue()==1;
        } else if (right instanceof XLocal) {
            // Might we need to know what values flow into this RHS operand?
        } else if (right instanceof XField) {
            // Might we need to know what values flow into this RHS operand?
            XField rightField= (XField) right;
            XVar rightRcvr= rightField.receiver();
            //Object rightName= rightField.field();

            if (rightRcvr instanceof XLocal) {
                XLocal rightRcvrLocal= (XLocal) rightRcvr;
                warn("Hey!");
            }
        }
        return false;
    }

    private static Position PCG= Position.COMPILER_GENERATED;

    private IntLit intLit(int i) {
        return (IntLit) xnf.IntLit(PCG, IntLit.INT, i).type(xts.Int());
    }

    private Id id(String name) {
        return xnf.Id(PCG, name);
    }

    private CanonicalTypeNode intTypeNode() {
        return xnf.CanonicalTypeNode(PCG, xts.Int());
    }

    private FlagsNode finalFlag() {
        return xnf.FlagsNode(PCG, Flags.FINAL);
    }

    private FlagsNode noFlags() {
        return xnf.FlagsNode(PCG, Flags.NONE);
    }

    private Id id(Name name) {
        return xnf.Id(PCG, name);
    }

    private Local local(LocalDef def) {
        LocalInstance li = def.asInstance();
        return (Local) xnf.Local(PCG, id(li.name())).localInstance(li).type(li.type());
    }

    private LocalDecl localDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        LocalDef def= xts.localDef(PCG, Flags.NONE, Types.ref(xts.Int()), name);
        return xnf.LocalDecl(PCG, noFlags(), intTypeNode, id(name), init).localDef(def);
    }

    private LocalDecl finalLocalDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        LocalDef def= xts.localDef(PCG, Flags.FINAL, Types.ref(intTypeNode.type()), name);
        return xnf.LocalDecl(PCG, finalFlag(), intTypeNode, id(name), init).localDef(def);
    }

    /**
     * Creates a name with the given prefix that is unique within the given context.
     * The name will be just the prefix if that does not cause unintended name capture.
     * @param prefix
     * @param context
     * @return
     */
    private Name makeFreshInContext(String prefix) {
        Context ctx= context();
        Name name= Name.make(prefix);
        if (ctx.findVariableSilent(name) != null)
            return Name.makeFresh(prefix);
        return name;
    }

    private Expr div(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.DIV, right).type(xts.Int());
    }

    private Expr mod(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.MOD, right).type(xts.Int());
    }

    private Expr mul(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.MUL, right).type(xts.Int());
    }

    private Expr plus(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.ADD, right).type(xts.Int());
    }

    private Expr sub(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.SUB, right).type(xts.Int());
    }

    private Expr lt(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.LT, right).type(xts.Boolean());
    }

    private Expr le(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.LE, right).type(xts.Boolean());
    }

    private Expr eq(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.EQ, right).type(xts.Boolean());
    }

    private Expr neq(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.NE, right).type(xts.Boolean());
    }

    private Expr ge(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.GE, right).type(xts.Boolean());
    }

    private Expr gt(Expr left, Expr right) {
        return xnf.Binary(PCG, left, Binary.GT, right).type(xts.Boolean());
    }

    private Expr addAssign(Expr target, Expr source) {
        return xnf.Assign(PCG, target, Assign.ADD_ASSIGN, source).type(target.type());
    }

    private Expr subAssign(Expr target, Expr source) {
        return xnf.Assign(PCG, target, Assign.SUB_ASSIGN, source).type(target.type());
    }

    private Expr mulAssign(Expr target, Expr source) {
        return xnf.Assign(PCG, target, Assign.MUL_ASSIGN, source).type(target.type());
    }

    private Expr divAssign(Expr target, Expr source) {
        return xnf.Assign(PCG, target, Assign.DIV_ASSIGN, source).type(target.type());
    }

    private Expr modAssign(Expr target, Expr source) {
        return xnf.Assign(PCG, target, Assign.MOD_ASSIGN, source).type(target.type());
    }

    private Expr postInc(Expr target) {
        return xnf.Unary(PCG, target, Unary.POST_INC).type(xts.Int());
    }

    private Expr postDec(Expr target) {
        return xnf.Unary(PCG, target, Unary.POST_DEC).type(xts.Int());
    }

    private Expr preInc(Expr target) {
        return xnf.Unary(PCG, target, Unary.PRE_INC).type(xts.Int());
    }

    private Expr preDec(Expr target) {
        return xnf.Unary(PCG, target, Unary.PRE_DEC).type(xts.Int());
    }

    private Expr not(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.NOT).type(xts.Boolean());
    }

    private Expr neg(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.NEG).type(expr.type());
    }

    private Expr bitNot(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.BIT_NOT).type(xts.Int());
    }

    private Stmt eval(Expr expr) {
        return xnf.Eval(PCG, expr);
    }

    private Stmt unrollLoop(ForLoop fLoop) {
        if (!checkPreconditions(fLoop))
            return fLoop;
        assert (fLoopParams.fExtentUnknown);
        // Have to do this the hard way: Generate code to ask the loop domain for its min and max,
        // and use those bounds to drive an ordinary for-loop. Also have to re-generate the loop
        // to take care of the tail end of the iteration domain (if the unroll factor doesn't
        // evenly divide the iteration bounds).
        // The code will look like this:
        //
        // {
        //   int min = r.min(0);
        //   int max = r.max(0);
        //   int loopMax = (max - min + 1) / `fUnrollFactor` * `fUnrollFactor` + min;
        //   for(int loopVar = min; loopVar < loopMax; loopVar += `fUnrollFactor`) { // [(min..max) : `fUnrollFactor`]
        //      loopBody
        //      loopBody[loopVar+1/loopVar]
        //      ...
        //      loopBody[loopVar+`fUnrollFactor`-1/loopVar]
        //   }
        // 
        //   for(int loopVar = loopMax; loopVar < max; loopVar++) {
        //      loopBody
        //   }
        // }
        Expr domain= fLoopParams.fLoopDomain;
        Expr domainMin= (fLoopParams.fMinSymbolic != null) ? fLoopParams.fMinSymbolic : xnf.Call(PCG, domain, id("min"), intLit(0));
        Expr domainMax= (fLoopParams.fMaxSymbolic != null) ? fLoopParams.fMaxSymbolic : xnf.Call(PCG, domain, id("max"), intLit(0));
        LocalDecl minDecl= finalLocalDecl(makeFreshInContext("min"), intTypeNode(), domainMin);
        LocalDecl maxDecl= finalLocalDecl(makeFreshInContext("max"), intTypeNode(), domainMax);
        ContextVisitor desugarer= new Desugarer(job, xts, xnf).context(this.context());
        Expr loopMax= plus(mul(div(plus(sub(local(maxDecl.localDef()), local(minDecl.localDef())), intLit(1)), intLit(fUnrollFactor)), intLit(fUnrollFactor)), local(minDecl.localDef()));
        loopMax= (Expr) loopMax.visit(desugarer);
        LocalDecl loopMaxDecl= finalLocalDecl(makeFreshInContext("loopMax"), intTypeNode(), loopMax);

        //Formal firstDimVar= ((X10Formal) fLoopParams.fLoopVar).vars().get(0);
        Formal firstDimVar= (X10Formal) fLoopParams.fLoopVar;
        Name loopVarName= makeFreshInContext(firstDimVar.name().toString());
        LocalDecl newLoopVarInit= localDecl(loopVarName, intTypeNode(), local(minDecl.localDef()));
        Expr loopCond= lt(local(newLoopVarInit.localDef()), local(loopMaxDecl.localDef()));
        loopCond= (Expr) loopCond.visit(desugarer);
        ForUpdate loopUpdate= (ForUpdate) eval(addAssign(local(newLoopVarInit.localDef()), intLit(fUnrollFactor)));
        loopUpdate= (ForUpdate) loopUpdate.visit(desugarer);
        List<ForInit> newLoopInits= Arrays.<ForInit>asList(newLoopVarInit);
        List<ForUpdate> newLoopUpdates= Arrays.asList(loopUpdate);
        List<Stmt> newLoopBodyStmts= new ArrayList<Stmt>(fUnrollFactor);
        X10Formal loopVar= (X10Formal) fLoopParams.fLoopVar;

        final Map<LocalDef, Expr> subs= CollectionFactory.newHashMap(1);
        final Context outer= context();
        class ReplaceLoopVar extends Desugarer.Substitution<Expr> {
            public ReplaceLoopVar() {
                super(Expr.class, null);
            }
            protected Expr subst(Expr n) {
                if (n instanceof Local) {
                    Local l = (Local) n;
                    Context ctx =  context();
                    while (ctx != outer) {
                        if (ctx.findVariableInThisScope(l.name().id()) != null) {
                            // TODO Do something more sensible than just throwing an exception
                            // This info needs to get back to the user in consumable form
                            throw new IllegalStateException("Inlining failed: unintended name capture for " + l.name().id());
                        }
                    }
                    LocalDef ld = l.localInstance().def();
                    if (subs.containsKey(ld)) {
                        return subs.get(ld);
                    }
                }
                return n;
            }
        }
        for(int i= 0; i < fUnrollFactor; i++) {
            Stmt subbedBody= fLoop.body();
            if (loopVar.type().type().isInt() || loopVar.vars().size() > 0) {
                Expr varValue= intLit(i);
                Expr newInit= plus(local(newLoopVarInit.localDef()), varValue);
                newInit= (Expr) newInit.visit(desugarer);
                subs.put(firstDimVar.localDef(), newInit);
                Desugarer.Substitution<Expr> subPerformer= new ReplaceLoopVar();
                subbedBody= (Stmt) subbedBody.visit(subPerformer);
            } else {
                subbedBody= (Stmt) subbedBody.copy();
            }
            Reinstantiator reinstantiator= new Reinstantiator(TypeParamSubst.IDENTITY);
            ContextVisitor visitor= new NodeTransformingVisitor(job, ts, nf, reinstantiator).context(context());
            subbedBody= (Stmt) subbedBody.visit(visitor); // reinstantiate locals in the body
            newLoopBodyStmts.add(subbedBody);
        }
        Block newLoopBody= xnf.Block(PCG, newLoopBodyStmts);
        For newForStmt= xnf.For(PCG, newLoopInits, loopCond, newLoopUpdates, newLoopBody);
        List<Stmt> unrollBlockStmts;

        if (fHandleLoopLeftovers == LoopLeftoverHandling.GENERATE_ASSERT) {
            Expr aexpr= eq(mod(plus(sub(local(maxDecl.localDef()), local(minDecl.localDef())), intLit(1)), intLit(fUnrollFactor)), intLit(0));
            aexpr= (Expr) aexpr.visit(desugarer);
            Stmt assertStmt= xnf.Assert(PCG, aexpr);

            unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, assertStmt, newForStmt);
        } else {
            // Now for the "remainder loop":
            //   for(int loopVar = (max / `fUnrollFactor`) * `fUnrollFactor`; loopVar < max; loopVar++) {
            //      loopBody
            //   }
            Expr remainderLoopMinIdx= local(loopMaxDecl.localDef());
            LocalDecl remainderLoopInit= localDecl(loopVarName, intTypeNode(), remainderLoopMinIdx);
            Expr remainderCond= le(local(remainderLoopInit.localDef()), local(maxDecl.localDef()));
            remainderCond= (Expr) remainderCond.visit(desugarer);
            ForUpdate remainderUpdate= (ForUpdate) eval(postInc(local(remainderLoopInit.localDef())));
            remainderUpdate= (ForUpdate) remainderUpdate.visit(desugarer);
            Stmt subbedBody= fLoop.body();
            if (loopVar.type().type().isInt() || loopVar.vars().size() > 0) {
                Expr newInit= local(remainderLoopInit.localDef());
                subs.put(firstDimVar.localDef(), newInit);
                Desugarer.Substitution<Expr> subPerformer= new ReplaceLoopVar();
                subbedBody= (Stmt) subbedBody.visit(subPerformer);
            } else {
                subbedBody= (Stmt) subbedBody.copy();
            }
            Stmt remainderLoop= xnf.For(PCG, Arrays.<ForInit>asList(remainderLoopInit), remainderCond, Arrays.asList(remainderUpdate), subbedBody);

            unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, newForStmt, remainderLoop);
        }

        return xnf.Block(PCG, unrollBlockStmts);
    }

    protected int findIndexInParent(Stmt s, Block parent) {
        for(int stmtIdx= 0; stmtIdx < parent.statements().size(); stmtIdx++) {
            if (parent.statements().get(stmtIdx) == s) {
                return stmtIdx;
            }
        }
        return -1;
    }

    public void setUnrollFactor(int factor) {
        fUnrollFactor= factor;
    }

    public void setAddAssertion() {
        fHandleLoopLeftovers= LoopLeftoverHandling.GENERATE_ASSERT;
    }

    public void setGenerateTailLoop() {
        fHandleLoopLeftovers= LoopLeftoverHandling.GENERATE_TAIL_LOOP;
    }
}
