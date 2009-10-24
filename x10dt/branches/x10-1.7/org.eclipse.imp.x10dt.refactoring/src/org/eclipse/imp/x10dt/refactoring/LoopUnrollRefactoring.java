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
import org.eclipse.imp.x10dt.refactoring.changes.InsertStatementChange;
import org.eclipse.imp.x10dt.refactoring.changes.ReplaceExpressionChange;
import org.eclipse.imp.x10dt.refactoring.changes.ReplaceStatementChange;
import org.eclipse.imp.x10dt.refactoring.transforms.Simplifier;
import org.eclipse.imp.x10dt.refactoring.transforms.SubstitutionPerformer;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FlagsNode;
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
import polyglot.ast.VarDecl;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.RegionMaker;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.X10Flags;
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
            return fatalStatus("You must select a single loop statement.");
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
            if (isRegionConvertCall(mi)) {
                List<Expr> args= call.arguments();
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

        if (domain instanceof Field) {
            Field f= (Field) domain;
            if (f.name().toString().equals("dist")) {
                Receiver rcvr= f.target();
                if (rcvr instanceof Local) {
                    domainLocal= ((Local) rcvr);
                } else {
                    return fatalStatus("Unhandled case: for-loop domain is a field access via a non-local variable");
                }
            } else {
                return fatalStatus("Unhandled case: for-loop domain is not a field access non-local variable");
            }
        } else if (domain instanceof Local) {
            domainLocal= (Local) domain;
        } else {
            return fatalStatus("Unhandled case: for-loop domain is neither a local var nor a 'dist' field access");
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
        return okStatus();
    }

    private boolean checkDomainIs1D(Expr domain) {
        ConstrainedType type= (ConstrainedType) domain.type();
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

    private Id id(Name name) {
        return fNodeFactory.Id(PCG, name);
    }

    private Local local(Name name) {
        return fNodeFactory.Local(PCG, id(name));
    }

    private LocalDecl finalLocalDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        return fNodeFactory.LocalDecl(PCG, finalFlag(), intTypeNode, id(name), init);
    }

    private LocalDecl valueLocalDecl(Name name, CanonicalTypeNode intTypeNode, Expr init) {
        return fNodeFactory.LocalDecl(PCG, valueFlag(), intTypeNode, id(name), init);
    }

    /**
     * Creates a name with the given prefix that is unique within the given context.
     * The name will be just the prefix if that does not cause unintended name capture.
     * @param prefix
     * @param context
     * @return
     */
    private Name makeFreshInContext(String prefix, Node context) {
        return Name.makeFresh(prefix);
    }

    private void createUnrollChange(org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange) {
        Block loopParent= (Block) fPathComputer.getParent(fLoop);
        int stmtIdx= findIndexInParent(fLoop, loopParent);

        if (fLoopParams.fExtentUnknown) {
            // Have to do this the hard way: Generate code to ask the loop domain for its min and max,
            // wrap unrolled loop body copies in a dynamic check that it can be unrolled that many times,
            // and re-generate the loop to take care of the remainder of the iteration domain.
            // The code will look like this:
            //
            // {  min = loopRegion.min(0);
            //    max = loopRegion.max(0);
            //    if (max > min + `fUnrollFactor`) {
            //        loopBody[min / loopVar]
            //        loopBody[min+1 / loopVar]
            //        ...
            //        loopBody[min+`fUnrollFactor`-1 / loopVar]
            //    }
            //    for(loopVar in [`fUnrollFactor`+1 .. max]) {
            //        loopBody
            //    }
            // }
            //
            Expr domain= fLoopParams.fLoopDomain;
            Expr domainMinCall= fNodeFactory.Call(PCG, domain, id("min"), intLit(0));
            Expr domainMaxCall= fNodeFactory.Call(PCG, domain, id("max"), intLit(0));
            Name minName= makeFreshInContext("min", fLoop.body());
            Name maxName= makeFreshInContext("max", fLoop.body());
            Stmt minDecl= valueLocalDecl(minName, intTypeNode(), domainMinCall);
            Stmt maxDecl= valueLocalDecl(maxName, intTypeNode(), domainMaxCall);
            Expr minPlusFactor= fNodeFactory.Binary(PCG, local(minName), Binary.ADD, intLit(fUnrollFactor));
            Expr ifCond= fNodeFactory.Binary(PCG, local(maxName), Binary.GT, minPlusFactor);
            List<Stmt> ifBodyStmts= new ArrayList<Stmt>(fUnrollFactor);

            for(int i= 0; i < fUnrollFactor; i++) {
                Map<VarInstance<VarDef>, Node> subs= new HashMap<VarInstance<VarDef>, Node>(1);
                Formal firstDimVar= ((X10Formal) fLoopParams.fLoopVar).vars().get(0);
                Expr varValue= intLit(i);
                subs.put((VarInstance) firstDimVar.localDef().asInstance(), varValue);
                SubstitutionPerformer subPerformer= new SubstitutionPerformer(subs);
                Stmt subbedBody = (Stmt) subPerformer.perform(fLoop.body(), fSourceAST);
                Simplifier simplifier= new Simplifier();
                Stmt simplifiedBody = (Stmt) subbedBody.visit(simplifier);

                ifBodyStmts.add(simplifiedBody);
            }
            Block ifBody= fNodeFactory.Block(PCG, ifBodyStmts);
            Stmt ifStmt= fNodeFactory.If(PCG, ifCond, ifBody);
            Expr remainderDomain= fNodeFactory.RegionMaker(PCG, intLit(fUnrollFactor+1), local(maxName));
            X10Formal remainderFormal= (X10Formal) fNodeFactory.Formal(PCG, fNodeFactory.FlagsNode(PCG, Flags.NONE), fNodeFactory.CanonicalTypeNode(PCG, fTypeSystem.Point()), fLoop.formal().name());
            Formal remainderFormal1stDim= fNodeFactory.Formal(PCG, fNodeFactory.FlagsNode(PCG, Flags.NONE), fNodeFactory.CanonicalTypeNode(PCG, fTypeSystem.Int()), id("d"));
            remainderFormal= remainderFormal.vars(Arrays.asList(remainderFormal1stDim));
            Stmt remainderLoop= fNodeFactory.ForLoop(PCG, remainderFormal, remainderDomain, (Stmt) fLoop.body().copy());
            Block unrollBlock= fNodeFactory.Block(PCG, Arrays.asList(minDecl, maxDecl, ifStmt, remainderLoop));

            outerChange.add(new ReplaceStatementChange("Unroll loop body", fLoop, unrollBlock, loopParent));
            return;
        }

        if (fLoopParams.fStride <= 0) {
            return;
        }

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
}
