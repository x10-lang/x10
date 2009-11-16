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

package org.eclipse.imp.x10dt.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.x10dt.refactoring.analysis.ReachingDefsVisitor;
import org.eclipse.imp.x10dt.refactoring.analysis.ReachingDefsVisitor.ValueMap;
import org.eclipse.imp.x10dt.refactoring.changes.AddAnnotationChange;
import org.eclipse.imp.x10dt.refactoring.changes.CopyStatementChange;
import org.eclipse.imp.x10dt.refactoring.changes.FileChange;
import org.eclipse.imp.x10dt.refactoring.changes.ReplaceExpressionChange;
import org.eclipse.imp.x10dt.refactoring.changes.ReplaceStatementChange;
import org.eclipse.imp.x10dt.refactoring.transforms.Simplifier;
import org.eclipse.imp.x10dt.refactoring.transforms.SubstitutionPerformer;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.RegionMaker;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.X10Flags;
import polyglot.frontend.Globals;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XEquals;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XVar;

public class LoopUnrollRefactoring extends AnnotationRefactoringBase {
    private static final String LOOP_UNROLL_REFACTORING_NAME= "Loop Unroll";

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
     * provides information on reaching values for various
     */
    private ValueMap fValueMap;

    /**
     * determines how to handle trailing iterations that are left over by the unrolled loop
     */
    private LoopLeftoverHandling fHandleLoopLeftovers;

    public LoopUnrollRefactoring(ITextEditor editor) {
        super(editor);
    }

    @Override
    public String getName() {
        return LOOP_UNROLL_REFACTORING_NAME;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return fatalStatus("Unable to analyze source due to syntax errors");
        }
        if (fSelNodes.size() != 1) {
            return fatalStatus("You must select a single loop statement to unroll.");
        }
        Node node= fSelNodes.get(0);
        if (!(node instanceof ForLoop)) {
            return fatalStatus("Selected node is not a 'for' loop");
        }
        fLoop= (ForLoop) node;
        fPathComputer= new NodePathComputer(fSourceAST, fLoop);
        fContainingMethod= (X10MethodDecl) fPathComputer.findEnclosingNode(fLoop, MethodDecl.class);

        computeReachingDefs();

        return okStatus();
    }

    private void computeReachingDefs() {
        getNodeFactoryTypeSystem();

        ReachingDefsVisitor rdVisitor = new ReachingDefsVisitor(fContainingMethod, null, fTypeSystem, fNodeFactory);

        fContainingMethod.visit(rdVisitor);
        fValueMap= rdVisitor.getReachingDefs();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            if (Globals.Compiler() == null) {
                Globals.initialize(fTypeSystem.extensionInfo().compiler());
            }
            RefactoringStatus status= findLoopParams();
            if (status.isOK()) {
                status= checkForInductionVarRefs();
            }
            return status;
        } catch (Exception e) {
            return fatalStatus(e.getMessage());
        }
    }

    private RefactoringStatus checkForInductionVarRefs() {
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

    private RefactoringStatus findLoopParams() {
        fLoopParams= new LoopParams(fLoop.formal(), fLoop.domain());

        X10Formal loopVar = (X10Formal) fLoop.formal();
        List<Formal> explodedVars= loopVar.vars();

        if (explodedVars.size() > 1) {
            return fatalStatus("Exploded variable syntax is not yet supported for multi-dimensional loop iteration domains");
        }

        // use type of domain to check its rank
        if (!checkDomainIs1D(fLoop.domain())) {
            return fatalStatus("Cannot statically confirm that loop iteration domain is 1-dimensional");
        }

        // now find the values that flow into the domain expr
        RefactoringStatus status= findDomainValues();

        if (!status.isOK()) {
            return status;
        }

        // now examine the domain values to determine the loop bounds
        return extractDomainBounds();
    }

    private RefactoringStatus extractDomainBounds() {
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
        if (e instanceof Local) { // always handled by above?
            Set<Term> localVals= fValueMap.getValues(((Local) e).localInstance().def());

            if (localVals.size() == 1) {
                Expr localVal= (Expr) localVals.iterator().next();
                return getConstantValueOf(localVal);
            }
        }
        return -1; // TOTALLY BOGUS - probably need to return a RefactoringStatus to abort the refactoring
    }

    private RefactoringStatus findDomainValues() {
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
            VarDef localDef= domainLocal.localInstance().def();
            Set<Term> domainTerms= fValueMap.getValues(localDef);

            for(Term t: domainTerms) {
                if (t instanceof Expr) {
                    fLoopParams.addDomainValue((Expr) t);
                } else {
                    fConsoleStream.println("Don't know what to do with domain term: " + t);
                }
            }
        } else if (domain instanceof Call) {
            fLoopParams.addDomainValue(domain);
        }
        return okStatus();
    }

    private boolean checkDomainIs1D(Expr domain) {
        ConstrainedType type= (ConstrainedType) domain.type();

        if (fTypeSystem.isRail(type)) {
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
                fConsoleStream.println("Hey!");
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

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange= new FileChange(LOOP_UNROLL_REFACTORING_NAME, fSourceFile.getFullPath().toOSString());

        if (getByAnnotation()) {
            createAddUnrollAnnotationChange(outerChange);
        } else {
            createUnrollChange(outerChange);
        }

        return interpretChange(outerChange);
    }

    private static Position PCG= Position.COMPILER_GENERATED;

    private IntLit intLit(int i) {
        return (IntLit) fNodeFactory.IntLit(PCG, IntLit.INT, i).type(fTypeSystem.Int());
    }

    private Id id(String name) {
        return fNodeFactory.Id(PCG, name);
    }

    private CanonicalTypeNode intTypeNode() {
        return fNodeFactory.CanonicalTypeNode(PCG, fTypeSystem.Int());
    }

    private FlagsNode finalFlag() {
        return fNodeFactory.FlagsNode(PCG, Flags.FINAL);
    }

    private FlagsNode valueFlag() {
        return fNodeFactory.FlagsNode(PCG, X10Flags.VALUE);
    }

    private FlagsNode noFlags() {
        return fNodeFactory.FlagsNode(PCG, Flags.NONE);
    }

    private Id id(Name name) {
        return fNodeFactory.Id(PCG, name);
    }

    private Local local(Name name) {
        return fNodeFactory.Local(PCG, id(name));
    }

    private LocalDecl localDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        return fNodeFactory.LocalDecl(PCG, noFlags(), intTypeNode, id(name), init);
    }

    private LocalDecl finalLocalDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        return fNodeFactory.LocalDecl(PCG, finalFlag(), intTypeNode, id(name), init);
    }

    private LocalDecl valueLocalDecl(Name name, CanonicalTypeNode typeNode, Expr init) {
        return fNodeFactory.LocalDecl(PCG, valueFlag(), typeNode, id(name), init);
    }

    /**
     * Creates a name with the given prefix that is unique within the given context.
     * The name will be just the prefix if that does not cause unintended name capture.
     * @param prefix
     * @param context
     * @return
     */
    private Name makeFreshInContext(String prefix, Node accessor) {
        NodePathComputer pc= new NodePathComputer(fSourceAST, accessor);
        Node parent= pc.getParent(accessor);

        while (parent != null) {
            if (parent instanceof Block) {
                // Look for clashing local decls
                Block block= (Block) parent;
                List<Stmt> stmts= block.statements();
                for(Stmt s: stmts) {
                    if (s instanceof LocalDecl) {
                        LocalDecl localDecl= (LocalDecl) s;
                        if (localDecl.name().toString().equals(prefix)) {
                            // Assume the worst: that this decl is before the accessor in question
                            return Name.makeFresh(prefix);
                        }
                    }
                }
            } else if (parent instanceof MethodDecl) {
                // Look for clashing formal arg decls
                MethodDecl methodDecl= (MethodDecl) parent;
                List<Formal> formals= methodDecl.formals();
                for(Formal f: formals) {
                    if (f.name().toString().equals(prefix)) {
                        return Name.makeFresh(prefix);
                    }
                }
            } else if (parent instanceof ClassDecl) {
                // Look for clashing field member decls
                ClassDecl classDecl= (ClassDecl) parent;
                List<ClassMember> members= classDecl.body().members();

                for(ClassMember m: members) {
                    if (m instanceof FieldDecl) {
                        FieldDecl fd= (FieldDecl) m;
                        if (fd.name().toString().equals(prefix)) {
                            return Name.makeFresh(prefix);
                        }
                    }
                }
            }
            parent= pc.getParent(parent);
        }
        // Don't need to check for bindings within accessor - if the prefix gets shadowed now, it would have been shadowed before
        return Name.make(prefix); // Name.makeFresh(prefix);
    }

    private Expr div(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.DIV, right);
    }

    private Expr mod(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.MOD, right).type(fTypeSystem.Int());
    }

    private Expr mul(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.MUL, right);
    }

    private Expr plus(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.ADD, right);
    }

    private Expr sub(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.SUB, right);
    }

    private Expr lt(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.LT, right).type(fTypeSystem.Boolean());
    }

    private Expr le(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.LE, right).type(fTypeSystem.Boolean());
    }

    private Expr eq(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.EQ, right).type(fTypeSystem.Boolean());
    }

    private Expr neq(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.NE, right).type(fTypeSystem.Boolean());
    }

    private Expr ge(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.GE, right).type(fTypeSystem.Boolean());
    }

    private Expr gt(Expr left, Expr right) {
        return fNodeFactory.Binary(PCG, left, Binary.GT, right).type(fTypeSystem.Boolean());
    }

    private Expr addAssign(Expr target, Expr source) {
        return fNodeFactory.Assign(PCG, target, Assign.ADD_ASSIGN, source);
    }

    private Expr subAssign(Expr target, Expr source) {
        return fNodeFactory.Assign(PCG, target, Assign.SUB_ASSIGN, source);
    }

    private Expr mulAssign(Expr target, Expr source) {
        return fNodeFactory.Assign(PCG, target, Assign.MUL_ASSIGN, source);
    }

    private Expr divAssign(Expr target, Expr source) {
        return fNodeFactory.Assign(PCG, target, Assign.DIV_ASSIGN, source);
    }

    private Expr modAssign(Expr target, Expr source) {
        return fNodeFactory.Assign(PCG, target, Assign.MOD_ASSIGN, source).type(fTypeSystem.Int());
    }

    private Expr postInc(Expr target) {
        return fNodeFactory.Unary(PCG, target, Unary.POST_INC).type(fTypeSystem.Int());
    }

    private Expr postDec(Expr target) {
        return fNodeFactory.Unary(PCG, target, Unary.POST_DEC).type(fTypeSystem.Int());
    }

    private Expr preInc(Expr target) {
        return fNodeFactory.Unary(PCG, target, Unary.PRE_INC).type(fTypeSystem.Int());
    }

    private Expr preDec(Expr target) {
        return fNodeFactory.Unary(PCG, target, Unary.PRE_DEC).type(fTypeSystem.Int());
    }

    private Expr not(Expr expr) {
        return fNodeFactory.Unary(PCG, expr, Unary.NOT).type(fTypeSystem.Boolean());
    }

    private Expr neg(Expr expr) {
        return fNodeFactory.Unary(PCG, expr, Unary.NEG);
    }

    private Expr bitNot(Expr expr) {
        return fNodeFactory.Unary(PCG, expr, Unary.BIT_NOT).type(fTypeSystem.Int());
    }

    private Stmt eval(Expr expr) {
        return fNodeFactory.Eval(PCG, expr);
    }

    private void createUnrollChange(org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange) {
        Block loopParent= (Block) fPathComputer.getParent(fLoop);
        int stmtIdx= findIndexInParent(fLoop, loopParent);

        if (fLoopParams.fExtentUnknown) {
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
            Expr domainMin= (fLoopParams.fMinSymbolic != null) ? fLoopParams.fMinSymbolic : fNodeFactory.Call(PCG, domain, id("min"), intLit(0));
            Expr domainMax= (fLoopParams.fMaxSymbolic != null) ? fLoopParams.fMaxSymbolic : fNodeFactory.Call(PCG, domain, id("max"), intLit(0));
            Name minName= makeFreshInContext("min", fLoop.body());
            Name maxName= makeFreshInContext("max", fLoop.body());
            Stmt minDecl= finalLocalDecl(minName, intTypeNode(), domainMin);
            Stmt maxDecl= finalLocalDecl(maxName, intTypeNode(), domainMax);
            Expr loopMax= plus(mul(div(plus(sub(local(maxName), local(minName)), intLit(1)), intLit(fUnrollFactor)), intLit(fUnrollFactor)), local(minName));
            Name loopMaxName= makeFreshInContext("loopMax", fLoop);
            Stmt loopMaxDecl= finalLocalDecl(loopMaxName, intTypeNode(), loopMax);

            Formal firstDimVar= ((X10Formal) fLoopParams.fLoopVar).vars().get(0);
            Name loopVarName= makeFreshInContext(firstDimVar.name().toString(), fLoop);
            ForInit newLoopVarInit= localDecl(loopVarName, intTypeNode(), local(minName));
            Expr loopCond= lt(local(loopVarName), local(loopMaxName));
            ForUpdate loopUpdate= (ForUpdate) eval(addAssign(local(loopVarName), intLit(fUnrollFactor)));
            List<ForInit> newLoopInits= Arrays.asList(newLoopVarInit);
            List<ForUpdate> newLoopUpdates= Arrays.asList(loopUpdate);
            List<Stmt> newLoopBodyStmts= new ArrayList<Stmt>(fUnrollFactor);
            X10Formal loopVar= (X10Formal) fLoopParams.fLoopVar;

            for(int i= 0; i < fUnrollFactor; i++) {
                if (loopVar.vars().size() > 0) {
                    Map<VarInstance<VarDef>, Node> subs= new HashMap<VarInstance<VarDef>, Node>(1);
                    Expr varValue= intLit(i);
                    subs.put((VarInstance) firstDimVar.localDef().asInstance(), plus(local(loopVarName), varValue));
                    SubstitutionPerformer subPerformer= new SubstitutionPerformer(subs);
                    Stmt subbedBody = (Stmt) subPerformer.perform(fLoop.body(), fSourceAST);

                    newLoopBodyStmts.add(subbedBody);
                } else {
                    newLoopBodyStmts.add((Stmt) fLoop.body().copy());
                }
            }
            Block newLoopBody= fNodeFactory.Block(PCG, newLoopBodyStmts);
            For newForStmt= fNodeFactory.For(PCG, newLoopInits, loopCond, newLoopUpdates, newLoopBody);
            List<Stmt> unrollBlockStmts;

            if (fHandleLoopLeftovers == LoopLeftoverHandling.GENERATE_ASSERT) {
                Stmt assertStmt= fNodeFactory.Assert(PCG, eq(mod(plus(sub(local(maxName), local(minName)), intLit(1)), intLit(fUnrollFactor)), intLit(0)));

                unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, assertStmt, newForStmt);
            } else {
                // Now for the "remainder loop":
                //   for(int loopVar = (max / `fUnrollFactor`) * `fUnrollFactor`; loopVar < max; loopVar++) {
                //      loopBody
                //   }
                Expr remainderLoopMinIdx= local(loopMaxName);
                ForInit remainderLoopInit= localDecl(loopVarName, intTypeNode(), remainderLoopMinIdx);
                Expr remainderCond= le(local(loopVarName), local(maxName));
                ForUpdate remainderUpdate= (ForUpdate) eval(postInc(local(loopVarName)));
                Stmt remainderLoop= fNodeFactory.For(PCG, Arrays.asList(remainderLoopInit), remainderCond, Arrays.asList(remainderUpdate), (Stmt) fLoop.body().copy());

                unrollBlockStmts= Arrays.asList(minDecl, maxDecl, loopMaxDecl, newForStmt, remainderLoop);
            }

            Block unrollBlock= fNodeFactory.Block(PCG, unrollBlockStmts);
            
            outerChange.add(new ReplaceStatementChange("Unroll loop body", fLoop, unrollBlock, loopParent));
            return;
        }

        if (fLoopParams.fStride <= 0) {
            return;
        }

        if (true) throw new UnsupportedOperationException("Fix Me");
        // HACK HACK HACK

        int unrollIdx= 0;
        for(int i= fLoopParams.fMin; i < fLoopParams.fMax && unrollIdx < fUnrollFactor; i += fLoopParams.fStride, unrollIdx++) {
            // TODO Handle substitution for refs to Point induction var
            Map<VarInstance<VarDef>, Node> subs= new HashMap<VarInstance<VarDef>, Node>(1);
            Expr varValue= fNodeFactory.IntLit(Position.COMPILER_GENERATED, IntLit.INT, i).type(fTypeSystem.Int());
            Formal firstDimVar= ((X10Formal) fLoopParams.fLoopVar).vars().get(0);
            subs.put((VarInstance) firstDimVar.localDef().asInstance(), varValue);

            SubstitutionPerformer subPerformer= new SubstitutionPerformer(subs);
            Stmt subbedBody = (Stmt) subPerformer.perform(fLoop.body(), fSourceAST);
            Simplifier simplifier= new Simplifier();
            Stmt simplifiedBody = (Stmt) subbedBody.visit(simplifier);
            // TODO Use InsertStatementChange instead? The thing we're copying doesn't exist yet in the target AST...
            CopyStatementChange copyChange= new CopyStatementChange("Copy loop body", simplifiedBody, loopParent, stmtIdx);

            outerChange.add(copyChange);
        }
        // unrollIdx now has the lower bound for the remainder of the iteration domain
        Expr newIterRegion= fNodeFactory.RegionMaker(Position.COMPILER_GENERATED,
                fNodeFactory.IntLit(Position.COMPILER_GENERATED, IntLit.INT, unrollIdx),
                fLoopParams.fMaxSymbolic);
        ReplaceExpressionChange replaceChg= new ReplaceExpressionChange("Update iteration domain", fLoop.domain(), fLoop, newIterRegion);
        outerChange.add(replaceChg);
    }

    protected int findIndexInParent(Stmt s, Block parent) {
        for(int stmtIdx= 0; stmtIdx < parent.statements().size(); stmtIdx++) {
            if (parent.statements().get(stmtIdx) == s) {
                return stmtIdx;
            }
        }
        return -1;
    }

    private void createAddUnrollAnnotationChange(org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange) {
        outerChange.add(new AddAnnotationChange("Add unroll annotation", fLoop, "@Unroll(N)"));
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
