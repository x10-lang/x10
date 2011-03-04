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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.refactoring.actions.MarkContextAction;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.jface.text.Position;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.LocalDef;
import polyglot.visit.NodeVisitor;
import x10.ast.X10MethodDecl;
import x10.effects.EffectsVisitor;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Pair;

public class ExtractAsyncRefactoring extends X10RefactoringBase {
    private static final String EXTRACT_ASYNC_REFACTORING_NAME= "Extract Async";

    private NodePathComputer fPathComputer;

    private List<Node> fContextNodes;

    public ExtractAsyncRefactoring(ITextEditor editor) {
        super(editor);
    }

    @Override
    public String getName() {
        return EXTRACT_ASYNC_REFACTORING_NAME;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors");
        }
        Position contextPos= MarkContextAction.getCurrentContext((ITextEditor) fEditor);

        MarkContextAction.clearCurrentContext((ITextEditor) fEditor);

        if (contextPos == null) {
            return RefactoringStatus.createFatalErrorStatus("You must first select the finish context with 'Mark Finish Context'.");
        }
        if (!containsAll(contextPos, fSelNodes)) {
            return RefactoringStatus.createFatalErrorStatus("The finish context must contain the code to be extracted to an 'async'.");
        }

        Node anySelNode= fSelNodes.get(0);
        fPathComputer= new NodePathComputer(fSourceAST, anySelNode);
        fContainingMethod= (X10MethodDecl) fPathComputer.findEnclosingNode(anySelNode, MethodDecl.class);

        Node contextNode= (Node) fNodeLocator.findNode(fSourceAST, contextPos.offset, contextPos.offset + contextPos.length-1);

        if (contextNode == null || !(contextNode instanceof Stmt || contextNode instanceof Block)) {
            return RefactoringStatus.createFatalErrorStatus("The selected context must be a set of statements or a block.");
        }

        fContextNodes= findContainedNodes(contextNode, contextPos.offset, contextPos.offset + contextPos.length - 1);

        List<Expr> nonFinalVarRefs= refsToNonFinalVars(fSelNodes);

        if (nonFinalVarRefs.size() > 0) {
            RefactoringStatus status= RefactoringStatus.createErrorStatus("The selected code contains references to non-final variables.");
            for(Expr ref: nonFinalVarRefs) {
                addErrorAnnotation(ref.position(), "Reference to non-final variable '" + ((Local) ref).name() + "'", status);
            }
            return status;
        }

        List<Node> followingScopeSiblings= collectScopeSiblings();
        List<Expr> scopedVarRefs= refsToScopedVars(fSelNodes, followingScopeSiblings);

        if (scopedVarRefs.size() > 0) {
            RefactoringStatus status= RefactoringStatus.createErrorStatus("The selected code contains declarations of variables used outside the proposed finish/async: " + scopedVarRefs);
            Set<LocalDef> localDefs= new HashSet<LocalDef>();

            for(Expr ref: scopedVarRefs) {
                Local local= (Local) ref;

                addErrorAnnotation(ref.position(), "Reference to variable '" + local.name() + "' would be outside of proposed finish/async scope", status);
                localDefs.add(local.localInstance().def());
            }
            for(LocalDef localDef: localDefs) {
                addErrorAnnotation(localDef.position(), "Variable '" + localDef.name() + "' has references outside of proposed finish/async scope", status);
            }
            return status;
        }
        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    private List<Node> collectScopeSiblings() {
        List<Node> result= new ArrayList<Node>();
        Node selNodeParent= fPathComputer.getParent(fSelNodes.get(0));

        if (selNodeParent instanceof Block) {
            Block scope= (Block) selNodeParent;
            int idx= 0;
            Node lastSelStmt= fSelNodes.get(fSelNodes.size()-1);
            List<Stmt> stmts= scope.statements();
            for(; idx < stmts.size(); idx++) {
                if (stmts.get(idx) == lastSelStmt) {
                    break;
                }
            }
            for(idx++; idx < stmts.size(); idx++) {
                result.add(stmts.get(idx));
            }
        }
        return result;
    }

    private List<Expr> refsToScopedVars(List<Node> selNodes, List<Node> scopeSiblings) {
        final List<Expr> result= new ArrayList<Expr>();
        final List<LocalDef> scopedLocalDefs= new ArrayList<LocalDef>();

        for(Node node: selNodes) {
            node.visit(new NodeVisitor() {
                @Override
                public NodeVisitor enter(Node n) {
                    if (n instanceof LocalDecl) {
                        LocalDecl localDecl= (LocalDecl) n;
                        scopedLocalDefs.add(localDecl.localDef());
                    }
                    return super.enter(n);
                }
            });
        }
        for(Node node: scopeSiblings) {
            node.visit(new NodeVisitor() {
                @Override
                public NodeVisitor enter(Node n) {
                    if (n instanceof Local) {
                        Local local= (Local) n;
                        if (scopedLocalDefs.contains(local.localInstance().def())) {
                            result.add(local);
                        }
                    }
                    return super.enter(n);
                }
            });
        }

        return result;
    }

    private List<Expr> refsToNonFinalVars(List<Node> selNodes) {
        final List<Expr> result= new ArrayList<Expr>();
        final List<LocalDef> localDefs= new ArrayList<LocalDef>();
        // N.B. Need to omit local vars defined within the selected nodes
        for(Node node: selNodes) {
            node.visit(new NodeVisitor() {
                @Override
                public NodeVisitor enter(Node n) {
                    if (n instanceof LocalDecl) {
                        LocalDecl localDecl= (LocalDecl) n;
                        localDefs.add(localDecl.localDef());
                    }
                    if (n instanceof Local) {
                        Local local = (Local) n;
                        if (!local.localInstance().flags().isFinal() && !localDefs.contains(local.localInstance().def())) {
                            result.add(local);
                        }
                    }
                    return super.enter(n);
                }
            });
        }
        return result;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            List<Node> followingContextNodes= extractFollowingContext(fSelNodes, fContextNodes);

            // if there's no following context, there are no subsequent memory effects that need to be checked for commutativity
            if (followingContextNodes.size() > 0) {
                EffectsVisitor effVisitor= new EffectsVisitor(fContainingMethod /*, new ReachingDefsVisitor.ValueMap()*/);

                if (fVerbose) {
                    effVisitor.setVerbose(X10DTRefactoringPlugin.getInstance().getConsoleStream());
                }

                for(Node node: fSelNodes) {
                    node.visit(effVisitor);
                }
                for(Node node: followingContextNodes) {
                    node.visit(effVisitor);
                }

                if (fVerbose) {
                    effVisitor.dump();
                }
                Effect selNodesEff= effVisitor.accumulateEffectsFor(fSelNodes);
                Effect contextEff= effVisitor.accumulateEffectsFor(followingContextNodes);

                fConsoleStream.println("***");
                fConsoleStream.println("Effect of selected code = " + selNodesEff);
                fConsoleStream.println("Effect of context = " + contextEff);

                if (!selNodesEff.commutesWith(contextEff)) {
                    Set<Pair<Effect,Effect>> interference= selNodesEff.interferenceWith(contextEff);

                    fConsoleStream.println("***");
                    fConsoleStream.println("The following effects do not commute:");
                    for(Pair<Effect,Effect> p: interference) {
                        fConsoleStream.println(p.fst + " and " + p.snd);
                    }
                    Pair<Effect,Effect> first= interference.iterator().next();
                    return RefactoringStatus.createErrorStatus("The selected code contains effects that don't commute with the surrounding context, e.g. " + first.fst + " and " + first.snd);
                }
            }
            return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus("An exception occurred while analyzing the selected code: " + e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange outerChange = new CompositeChange(EXTRACT_ASYNC_REFACTORING_NAME);
        TextFileChange tfc = new TextFileChange("Wrap statements in 'async'", fSourceFile);

        tfc.setEdit(new MultiTextEdit());

        createAddAsyncChange(tfc, fSelNodes);
        createAddFinishChange(tfc, fContextNodes);
        outerChange.add(tfc);

        fFinalChange= outerChange;
        return fFinalChange;
    }
}
