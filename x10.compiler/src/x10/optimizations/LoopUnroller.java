/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

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
import polyglot.types.Context_c;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ForLoop;
import x10.ast.RegionMaker;
import x10.ast.Tuple;
import x10.ast.X10Formal;
import x10.ast.X10NodeFactory;
import x10.constraint.XConstraint;
import x10.constraint.XEquals;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.X10Flags;
import x10.types.X10TypeSystem;
import x10.visit.Desugarer;

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
        Set<Expr> fLoopDomainValues= new HashSet<Expr>();
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

    private final X10TypeSystem xts;

    private final X10NodeFactory xnf;

    public LoopUnroller(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
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
            if (Globals.Compiler() == null) {
                Globals.initialize(xts.extensionInfo().compiler());
            }
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
        return hasRefs[0] ? fatalStatus("Loop body has references to the Point loop induction variable") : okStatus();
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

        // now find the values that flow into the domain expr
        boolean status= findDomainValues();

        if (!status) {
            return status;
        }

        // now examine the domain values to determine the loop bounds
        return extractDomainBounds();
    }

    private boolean extractDomainBounds() {
        if (fLoopParams.fLoopDomainValues.size() != 1) {
            return fatalStatus("Can only analyze loop with 1 possible iteration domain value");
        }
        Expr v= fLoopParams.fLoopDomainValues.iterator().next();

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
                if (dimensionArgs.get(0) instanceof RegionMaker) {
                    RegionMaker regionMaker= (RegionMaker) dimensionArgs.get(0);

                    low= regionMaker.arguments().get(0);
                    hi= regionMaker.arguments().get(1);
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
            } else if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Region") &&
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
            } else {
                return fatalStatus("Don't understand iteration domain: " + call);
            }
        } else {
            if (!checkDomainIs1D(v)) {
                return fatalStatus("Cannot determine that iteration domain is 1-dimensional: " + fLoopParams.fLoopDomain);
            }
            fLoopParams.fExtentUnknown= true;
        }
        return okStatus();
    }

    private boolean isRegionConvertCall(MethodInstance mi) {
        return mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Region") &&
                mi.name().toString().equals("$convert");
    }

    private int getConstantValueOf(Expr e) {
        // TODO Need to produce the literal value here, but elsewhere need the symbolic value (e.g. when rewriting the region bounds)
        if (e.constantValue() != null && e.constantValue() instanceof Integer) {
            return ((Integer) e.constantValue()).intValue();
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

        if (xts.isRail(type)) {
            return true;
        }

        XConstraint typeCons= type.constraint().get();
        List<XTerm> consTerms= typeCons.constraints();

        XTerm rankConstraint= findRankConstraint(consTerms);
        if (rankConstraint != null) {
            if (constraintEq1(rankConstraint)) {
                return true;
            }
        }
        return false;
    }

    private boolean constraintEq1(XTerm term) {
        XEquals eq= (XEquals) term;
        List<XTerm> args= eq.arguments();
        XTerm left= args.get(0);
        XTerm right= args.get(1);

        if (right instanceof XLit) {
            XLit lit= (XLit) right;
            Object litVal= lit.val();
            return litVal.equals(new Integer(1));
        } else if (right instanceof XLocal) {
            // Might we need to know what values flow into this RHS operand?
        } else if (right instanceof XField) {
            // Might we need to know what values flow into this RHS operand?
            XField rightField= (XField) right;
            XVar rightRcvr= rightField.receiver();
            XName rightName= rightField.field();

            if (rightRcvr instanceof XLocal) {
                XLocal rightRcvrLocal= (XLocal) rightRcvr;
                warn("Hey!");
            }
        }
        return false;
    }

    private XTerm findRankConstraint(List<XTerm> terms) {
        for(XTerm term: terms) {
            if (term instanceof XEquals) {
                XEquals eq= (XEquals) term;
                List<XTerm> args= eq.arguments();
                XTerm left= args.get(0);
                XTerm right= args.get(1);

                if (left instanceof XField) {
                    XField leftField= (XField) left;
                    XVar leftRcvr= leftField.receiver();
                    XName leftName= leftField.field();

                    // HACK Would like to know whether leftRcvr is on "self", but the only indication seems to be
                    // that its name looks like "_selfNNNN"
                    boolean isSelf= leftRcvr instanceof XLocal && ((XLocal) leftRcvr).name().toString().contains("self");
                    if (leftName.toString().equals("x10.lang.Region#rank")) {
                        return term;
                    }
                }
            }
        }
        return null;
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

    private FlagsNode valueFlag() {
        return xnf.FlagsNode(PCG, X10Flags.VALUE);
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
        Expr loopMax= plus(mul(div(plus(sub(local(maxDecl.localDef()), local(minDecl.localDef())), intLit(1)), intLit(fUnrollFactor)), intLit(fUnrollFactor)), local(minDecl.localDef()));
        LocalDecl loopMaxDecl= finalLocalDecl(makeFreshInContext("loopMax"), intTypeNode(), loopMax);

        Formal firstDimVar= ((X10Formal) fLoopParams.fLoopVar).vars().get(0);
        Name loopVarName= makeFreshInContext(firstDimVar.name().toString());
        LocalDecl newLoopVarInit= localDecl(loopVarName, intTypeNode(), local(minDecl.localDef()));
        Expr loopCond= lt(local(newLoopVarInit.localDef()), local(loopMaxDecl.localDef()));
        ForUpdate loopUpdate= (ForUpdate) eval(addAssign(local(newLoopVarInit.localDef()), intLit(fUnrollFactor)));
        List<ForInit> newLoopInits= Arrays.<ForInit>asList(newLoopVarInit);
        List<ForUpdate> newLoopUpdates= Arrays.asList(loopUpdate);
        List<Stmt> newLoopBodyStmts= new ArrayList<Stmt>(fUnrollFactor);
        X10Formal loopVar= (X10Formal) fLoopParams.fLoopVar;

        for(int i= 0; i < fUnrollFactor; i++) {
            if (loopVar.vars().size() > 0) {
                final Map<VarInstance<VarDef>, Expr> subs= new HashMap<VarInstance<VarDef>, Expr>(1);
                Expr varValue= intLit(i);
                subs.put((VarInstance) firstDimVar.localDef().asInstance(), plus(local(newLoopVarInit.localDef()), varValue));
                final Context outer = context();
                Desugarer.Substitution<Expr> subPerformer= new Desugarer.Substitution<Expr>(Expr.class, null) {
                    protected Expr subst(Expr n) {
                        if (n instanceof Local) {
                            Local l = (Local) n;
                            Context_c ctx = (Context_c) context();
                            while (ctx != outer) {
                                if (ctx.findVariableInThisScope(l.name().id()) != null) {
                                    // TODO Do something more sensible than just throwing an exception
                                    // This info needs to get back to the user in consumable form
                                    throw new IllegalStateException("Inlining failed: unintended name capture for " + l.name().id());
                                }
                            }
                            VarInstance li = l.localInstance();
                            if (subs.containsKey(li)) {
                                return subs.get(li);
                            }
                        }
                        return n;
                    }
                };
                Stmt subbedBody = (Stmt) fLoop.body().visit(subPerformer);

                newLoopBodyStmts.add(subbedBody);
            } else {
                newLoopBodyStmts.add((Stmt) fLoop.body().copy());
            }
        }
        Block newLoopBody= xnf.Block(PCG, newLoopBodyStmts);
        For newForStmt= xnf.For(PCG, newLoopInits, loopCond, newLoopUpdates, newLoopBody);
        List<Stmt> unrollBlockStmts;

        if (fHandleLoopLeftovers == LoopLeftoverHandling.GENERATE_ASSERT) {
            Stmt assertStmt= xnf.Assert(PCG, eq(mod(plus(sub(local(maxDecl.localDef()), local(minDecl.localDef())), intLit(1)), intLit(fUnrollFactor)), intLit(0)));

            unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, assertStmt, newForStmt);
        } else {
            // Now for the "remainder loop":
            //   for(int loopVar = (max / `fUnrollFactor`) * `fUnrollFactor`; loopVar < max; loopVar++) {
            //      loopBody
            //   }
            Expr remainderLoopMinIdx= local(loopMaxDecl.localDef());
            LocalDecl remainderLoopInit= localDecl(loopVarName, intTypeNode(), remainderLoopMinIdx);
            Expr remainderCond= le(local(remainderLoopInit.localDef()), local(maxDecl.localDef()));
            ForUpdate remainderUpdate= (ForUpdate) eval(postInc(local(newLoopVarInit.localDef())));
            Stmt remainderLoop= xnf.For(PCG, Arrays.<ForInit>asList(remainderLoopInit), remainderCond, Arrays.asList(remainderUpdate), (Stmt) fLoop.body().copy());

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
