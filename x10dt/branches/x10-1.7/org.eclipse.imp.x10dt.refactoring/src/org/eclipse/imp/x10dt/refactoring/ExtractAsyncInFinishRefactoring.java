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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.refactoring.analysis.ReachingDefsVisitor;
import org.eclipse.imp.x10dt.refactoring.effects.EffectsVisitor;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.X10MethodDecl;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Pair;

public class ExtractAsyncInFinishRefactoring extends X10RefactoringBase {
    private static final String EXTRACT_ASYNC_IN_FINISH_REFACTORING_NAME= "Extract Async in Finish Scope";

    private NodePathComputer fPathComputer;

    private Finish fEnclFinish;

    public ExtractAsyncInFinishRefactoring(ITextEditor editor) {
        super(editor);
    }

    @Override
    public String getName() {
        return EXTRACT_ASYNC_IN_FINISH_REFACTORING_NAME;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors.");
        }
        if (fSelNodes.size() == 0 || !isSetOfStatements(fSelNodes)) {
            return RefactoringStatus.createFatalErrorStatus("You must select one or more statements");
        }
        Node anySelNode= fSelNodes.get(0); // could use any one of the selected nodes, since we're just looking for the parents
        fPathComputer= new NodePathComputer(fSourceAST, anySelNode);
        fContainingMethod= (X10MethodDecl) fPathComputer.findEnclosingNode(anySelNode, MethodDecl.class);
        fEnclFinish= fPathComputer.findEnclosingNode(anySelNode, Finish.class);

        if (fEnclFinish == null) {
            return RefactoringStatus.createFatalErrorStatus("Selected statement(s) not enclosed within a 'finish' statement.");
        }

        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    private boolean isSetOfStatements(List<Node> nodes) {
        for(Node node: nodes) {
            if (!(node instanceof Stmt)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            List<Node> followingContextNodes= extractFollowingContext(fSelNodes, Collections.singletonList((Node) fEnclFinish));
            EffectsVisitor effVisitor= new EffectsVisitor(new ReachingDefsVisitor.ValueMap(), fContainingMethod);

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
            return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus("An exception occurred while analyzing the selected code: " + e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange outerChange = new CompositeChange(EXTRACT_ASYNC_IN_FINISH_REFACTORING_NAME);
        TextFileChange tfc = new TextFileChange("Wrap statements in 'async'", fSourceFile);

        tfc.setEdit(new MultiTextEdit());

        createAddAsyncChange(tfc, fSelNodes);
        outerChange.add(tfc);

        fFinalChange= outerChange;
        return fFinalChange;
    }
}
