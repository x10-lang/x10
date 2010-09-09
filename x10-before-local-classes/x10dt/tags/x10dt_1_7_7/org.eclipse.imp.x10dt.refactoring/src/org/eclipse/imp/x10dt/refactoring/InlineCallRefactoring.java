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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.x10dt.refactoring.changes.AddAnnotationChange;
import org.eclipse.imp.x10dt.refactoring.changes.FileChange;
import org.eclipse.imp.x10dt.refactoring.changes.InlineCallChange;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ext.x10.ast.X10MethodDecl;

public class InlineCallRefactoring extends AnnotationRefactoringBase {
    private static final String LOOP_UNROLL_REFACTORING_NAME= "Inline Call";

    private Call fCall;

    private boolean fSimplify;

    public InlineCallRefactoring(ITextEditor editor) {
        super(editor);
    }

    @Override
    public String getName() {
        return LOOP_UNROLL_REFACTORING_NAME;
    }

    public void setSimplify(boolean simplify) {
        fSimplify= simplify;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return fatalStatus("Unable to analyze source due to syntax errors");
        }
        if (fSelNodes.size() != 1) {
            return fatalStatus("You must select a single expression.");
        }
        Node node= fSelNodes.get(0);
        fPathComputer= new NodePathComputer(fSourceAST, node);
        if (!(node instanceof Call)) {
            Node parent= fPathComputer.getParent(node);
            if (parent instanceof Call) {
                fCall= (Call) parent;
            } else {
                return fatalStatus("Selected node is not a method call");
            }
        } else {
            fCall= (Call) node;
        }
        fContainingMethod= (X10MethodDecl) fPathComputer.findEnclosingNode(fCall, MethodDecl.class);

        return okStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            RefactoringStatus status= okStatus();

            return status;
        } catch (Exception e) {
            return errorStatus(e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        getNodeFactoryTypeSystem();

        org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange= new FileChange(LOOP_UNROLL_REFACTORING_NAME, fSourceFile.getFullPath().toOSString());

        if (getByAnnotation()) {
            createAddInlineAnnotationChange(outerChange);
        } else {
            createInlineCallChange(outerChange);
        }

        return interpretChange(outerChange);
    }

    private void createInlineCallChange(org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange) {
        CodeBlock block= fPathComputer.findEnclosingNode(fCall, CodeBlock.class);
        outerChange.add(new InlineCallChange("Inline method call", fCall, block, fSourceAST, fSimplify));
    }

    private void createAddInlineAnnotationChange(org.eclipse.imp.x10dt.refactoring.changes.CompositeChange outerChange) {
        outerChange.add(new AddAnnotationChange("Add inline annotation", fCall, "@Inline"));
    }
}
