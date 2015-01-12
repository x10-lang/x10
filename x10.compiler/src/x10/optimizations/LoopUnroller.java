/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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
import polyglot.util.CollectionUtil; 
import x10.util.CollectionFactory;
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

    private class LoopParams {
        final VarDecl fLoopVar;
        final Expr fLoopDomain;
        Set<Expr> fLoopDomainValues= CollectionFactory.newHashSet();
        boolean fNumIterationsKnown;
        long fMin;
        Expr fMinSymbolic;
        long fMax;
        Expr fMaxSymbolic;
        long fStride;

        public LoopParams(VarDecl vd, Expr domain) {
            fLoopVar= vd;
            fLoopDomain= domain;
            fNumIterationsKnown= false;
        }

        public LoopParams(VarDecl vd, Expr domain, int min, int max, int stride) {
            this(vd, domain);
            fMin= min;
            fMax= max;
            fStride= stride;
            fNumIterationsKnown= true;
        }
    }

    /**
     * The user-selected ForLoop (often the same as fNode, if the latter is a ForLoop)
     */
    private ForLoop fLoop;

    /**
     * the user-supplied number of times to unroll the loop
     */
    private long fUnrollFactor;

    /**
     * records symbolic and constant information about the loop iteration domain
     */
    private LoopParams fLoopParams;

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
        assert (val instanceof Long);
        fUnrollFactor = ((Long) val).longValue();
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
        Expr domain = fLoop.domain();

        // use type of domain to check its rank
        if (!(ts.isIntRange(domain.type()) || ts.isLongRange(domain.type()))) {
            return fatalStatus("(at " + fLoopParams.fLoopVar.position() + ": cannot statically confirm that loop iteration domain is 1-dimensional");
        }

        return extractDomainBounds();
    }

    private boolean extractDomainBounds() {
    	Expr v= fLoopParams.fLoopDomain;
    	
        if (v instanceof Call) {
            Call call= (Call) v;
            MethodInstance mi= call.methodInstance();
            List<Expr> args= call.arguments();
            if (mi.name().equals(X10Binary_c.binaryMethodName(X10Binary_c.DOT_DOT)) && 
                    (mi.container().isInt() || mi.container().isLong())) {
                fLoopParams.fMinSymbolic= args.get(0);
                fLoopParams.fMaxSymbolic= args.get(1);
                fLoopParams.fStride= 1;
                if (fLoopParams.fMinSymbolic.isConstant() && fLoopParams.fMaxSymbolic.isConstant()) {
                    fLoopParams.fMin= fLoopParams.fMinSymbolic.constantValue().integralValue();
                    fLoopParams.fMax= fLoopParams.fMaxSymbolic.constantValue().integralValue();
                    fLoopParams.fNumIterationsKnown = true;
                }
            } else {
        		return fatalStatus("Don't understand iteration domain: " + call);
            }
        } else {
            return fatalStatus("Canont recognise loop domain for unrolling: " + fLoopParams.fLoopDomain);
        }
        return okStatus();
    }

    private static Position PCG= Position.COMPILER_GENERATED;

    private IntLit longLit(long i) {
        return (IntLit) xnf.IntLit(PCG, IntLit.LONG, i).type(xts.Long());
    }
    
    private IntLit intLit(long i) {
        return (IntLit) xnf.IntLit(PCG, IntLit.INT, i).type(xts.Int());
    }
    
    private IntLit lit(long i, Type t) {
        return t.isIntOrLess() ?  intLit(i) : longLit(i);
    }

    private Id id(String name) {
        return xnf.Id(PCG, name);
    }

    private CanonicalTypeNode makeTypeNode(Type t) {
        return xnf.CanonicalTypeNode(PCG, t);
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

    private LocalDecl localDecl(Name name, CanonicalTypeNode dTypeNode, Expr init) {
        LocalDef def= xts.localDef(PCG, Flags.NONE, Types.ref(dTypeNode.type()), name);
        return xnf.LocalDecl(PCG, noFlags(), dTypeNode, id(name), init).localDef(def);
    }

    private LocalDecl finalLocalDecl(Name name, CanonicalTypeNode dTypeNode, Expr init) {
        LocalDef def= xts.localDef(PCG, Flags.FINAL, Types.ref(dTypeNode.type()), name);
        return xnf.LocalDecl(PCG, finalFlag(), dTypeNode, id(name), init).localDef(def);
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

    private Type meetBinaryType(Type l, Type r) {
        if (l.isLong() && r.isLong()) return xts.Long();
        if (l.isLong() || r.isLong()) return xts.Long();
        return xts.Int();
    }
    
    private Expr div(Expr left, Expr right) {
        Type resType = meetBinaryType(left.type(), right.type());
        return xnf.Binary(PCG, left, Binary.DIV, right).type(resType);
    }

    private Expr mod(Expr left, Expr right) {
        Type resType = meetBinaryType(left.type(), right.type());
        return xnf.Binary(PCG, left, Binary.MOD, right).type(resType);
    }

    private Expr mul(Expr left, Expr right) {
        Type resType = meetBinaryType(left.type(), right.type());
        return xnf.Binary(PCG, left, Binary.MUL, right).type(resType);
    }

    private Expr plus(Expr left, Expr right) {
        Type resType = meetBinaryType(left.type(), right.type());
        return xnf.Binary(PCG, left, Binary.ADD, right).type(resType);
    }

    private Expr sub(Expr left, Expr right) {
        Type resType = meetBinaryType(left.type(), right.type());
        return xnf.Binary(PCG, left, Binary.SUB, right).type(resType);
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
        return xnf.Unary(PCG, target, Unary.POST_INC).type(target.type());
    }

    private Expr postDec(Expr target) {
        return xnf.Unary(PCG, target, Unary.POST_DEC).type(target.type());
    }

    private Expr preInc(Expr target) {
        return xnf.Unary(PCG, target, Unary.PRE_INC).type(target.type());
    }

    private Expr preDec(Expr target) {
        return xnf.Unary(PCG, target, Unary.PRE_DEC).type(target.type());
    }

    private Expr not(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.NOT).type(xts.Boolean());
    }

    private Expr neg(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.NEG).type(expr.type());
    }

    private Expr bitNot(Expr expr) {
        return xnf.Unary(PCG, expr, Unary.BIT_NOT).type(expr.type());
    }

    private Stmt eval(Expr expr) {
        return xnf.Eval(PCG, expr);
    }

    private Stmt unrollLoop(ForLoop fLoop) {
        if (!checkPreconditions(fLoop))
            return fLoop;
        
        long numIterations = fLoopParams.fNumIterationsKnown ? (fLoopParams.fMax - fLoopParams.fMin + 1)/fLoopParams.fStride : -1;
        Desugarer desugarer= (Desugarer) new Desugarer(job, xts, xnf).context(this.context());
        if (fLoopParams.fNumIterationsKnown && fUnrollFactor == numIterations) {
            // easy case: complete loop unroll
            VarDecl oldLoopVar = fLoopParams.fLoopVar;
            Name loopVarName= makeFreshInContext(oldLoopVar.name().toString());
            LocalDecl newLoopVarInit= localDecl(loopVarName, makeTypeNode(Types.baseType(fLoopParams.fMinSymbolic.type())), fLoopParams.fMinSymbolic);            
            List<Stmt> unrolledStatements = unrollBody(fLoop.body(), (int)fUnrollFactor, oldLoopVar, newLoopVarInit, desugarer);
            Block newLoopBody= xnf.Block(PCG, unrolledStatements);
            newLoopBody = newLoopBody.prepend(newLoopVarInit);
            return newLoopBody;
        } else {
            // General case: 
            // Generate code to ask the loop domain for its min and max,
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
            
            // Compute min, max, and loopMax
            Expr domainMin = fLoopParams.fMinSymbolic;
            Expr domainMax = fLoopParams.fMaxSymbolic;
            Type loopVarType = Types.baseType(fLoopParams.fMinSymbolic.type());
            LocalDecl minDecl= finalLocalDecl(makeFreshInContext("min"), makeTypeNode(loopVarType), domainMin);
            LocalDecl maxDecl= finalLocalDecl(makeFreshInContext("max"), makeTypeNode(loopVarType), domainMax);
            Expr loopMax;
            if (fLoopParams.fNumIterationsKnown) {
                loopMax = lit((numIterations / fUnrollFactor) * fUnrollFactor + fLoopParams.fMin, loopVarType);                
            } else {
                Type lvt = loopVarType;
                loopMax= plus(mul(div(plus(sub(local(maxDecl.localDef()), local(minDecl.localDef())), lit(1,lvt)), lit(fUnrollFactor, lvt)), lit(fUnrollFactor, lvt)), local(minDecl.localDef()));
                loopMax= (Expr) loopMax.visit(desugarer);
            }
            LocalDecl loopMaxDecl= finalLocalDecl(makeFreshInContext("loopMax"), makeTypeNode(loopVarType), loopMax);

            // Main loop, with body unrolled fUnrollFactor times
            VarDecl oldLoopVar = fLoopParams.fLoopVar;
            Name loopVarName= makeFreshInContext(oldLoopVar.name().toString());
            LocalDecl newLoopVarInit= localDecl(loopVarName, makeTypeNode(loopVarType), local(minDecl.localDef()));
            Expr loopCond= lt(local(newLoopVarInit.localDef()), local(loopMaxDecl.localDef()));
            loopCond= (Expr) loopCond.visit(desugarer);
            ForUpdate loopUpdate= (ForUpdate) eval(addAssign(local(newLoopVarInit.localDef()), lit(fUnrollFactor, loopVarType)));
            loopUpdate= (ForUpdate) loopUpdate.visit(desugarer);
            List<ForInit> newLoopInits= Arrays.<ForInit>asList(newLoopVarInit);
            List<ForUpdate> newLoopUpdates= Arrays.asList(loopUpdate);        
            List<Stmt> newLoopBodyStmts = unrollBody(fLoop.body(), (int)fUnrollFactor, oldLoopVar, newLoopVarInit, desugarer);
            Block newLoopBody= xnf.Block(PCG, newLoopBodyStmts);
            For newForStmt= xnf.For(PCG, newLoopInits, loopCond, newLoopUpdates, newLoopBody);

            // Now for the "remainder loop":
            //   for(int loopVar = (max / `fUnrollFactor`) * `fUnrollFactor`; loopVar < max; loopVar++) {
            //      loopBody
            //   }
            List<Stmt> unrollBlockStmts;
            if (fLoopParams.fNumIterationsKnown && (numIterations % fUnrollFactor == 0)) {
                // Divides evenly; don't need a remainder loop
                unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, newForStmt);                
            } else {
                Expr remainderLoopMinIdx= local(loopMaxDecl.localDef());
                LocalDecl remainderLoopInit= localDecl(loopVarName, makeTypeNode(loopVarType), remainderLoopMinIdx);
                Expr remainderCond= le(local(remainderLoopInit.localDef()), local(maxDecl.localDef()));
                remainderCond= (Expr) remainderCond.visit(desugarer);
                ForUpdate remainderUpdate= (ForUpdate) eval(postInc(local(remainderLoopInit.localDef())));
                remainderUpdate= (ForUpdate) remainderUpdate.visit(desugarer);
                Stmt subbedBody= unrollBody(fLoop.body(), 1, oldLoopVar, remainderLoopInit, desugarer).get(0);
                Stmt remainderLoop= xnf.For(PCG, Arrays.<ForInit>asList(remainderLoopInit), remainderCond, Arrays.asList(remainderUpdate), subbedBody);
                unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, newForStmt, remainderLoop);
            }

            return xnf.Block(PCG, unrollBlockStmts);
        }
    }

    private List<Stmt> unrollBody(Stmt body, int numCopies, VarDecl oldLoopVar, VarDecl newLoopVar,
                                  Desugarer desugarer) {
        List<Stmt> newLoopBodyStmts= new ArrayList<Stmt>(numCopies);

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
        for(int i= 0; i < numCopies; i++) {
            Stmt subbedBody= fLoop.body();
            if (newLoopVar.type().type().isLongOrLess()) {
                Expr varValue= lit(i, newLoopVar.type().type());
                Expr newInit= plus(local(newLoopVar.localDef()), varValue);
                newInit= (Expr) newInit.visit(desugarer);
                subs.put(oldLoopVar.localDef(), newInit);
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
        return newLoopBodyStmts;
    }
}
