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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.types.Def;

public class MoveRefactoring extends X10RefactoringBase {
    private static final String MOVE_REFACTORING_NAME= "Move";

    private final Map<Node,Def> fNodesToDefs;
    private final Map<Node,Node> fNodesToParents;

    private Node fTarget;

    public MoveRefactoring(ITextEditor editor) {
        super(editor);

        determineSelectedNodes();

        fNodesToDefs= new HashMap<Node, Def>(fSelNodes.size());
        fNodesToParents= new HashMap<Node, Node>(fSelNodes.size());

        for(Node node: fSelNodes) {
            fPathComputer= new NodePathComputer(fSourceAST, node);
            Node nodeParent= fPathComputer.getParent(node);
            Def def;

            fNodesToParents.put(node, nodeParent);

            if (node instanceof FieldDecl) {
                FieldDecl fieldDecl= (FieldDecl) node;
                def= fieldDecl.fieldDef();
            } else if (node instanceof Formal) {
                Formal f= (Formal) node;
                def = f.localDef();
            } else if (node instanceof MethodDecl) {
                MethodDecl method= (MethodDecl) node;
                def= method.methodDef();
            } else {
                def= null;
            }
            fNodesToDefs.put(node, def);
        }
    }

    protected Node getTarget() {
        return fTarget;
    }

    protected void setTarget(Node target) {
        fTarget= target;
    }

    @Override
    public String getName() {
        return MOVE_REFACTORING_NAME;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (true) {
            return RefactoringStatus.createFatalErrorStatus("Unsupported refactoring");
        }
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors");
        }

        for(Node node: fSelNodes) {
            if (fNodesToDefs.get(node) == null) {
                return RefactoringStatus.createErrorStatus("Unable to move declaration of " + node);
            }
        }
        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            RefactoringStatus status= checkForCollisions();
            if (status.isOK()) {
                status= checkVisibility();
            }
            return status;
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus("An exception occurred while analyzing the selected code: " + e.getMessage());
        }
    }

    private enum VisibilityChange {
        NO_CHANGE,
        TO_PROTECTED,
        TO_PUBLIC,
        ERROR
    }

    private Map<Def,VisibilityChange> fVisibilityChanges;

    private RefactoringStatus checkVisibility() {
        // populate fVisibilityChanges, return error status if any is VisibilityChange.ERROR
        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    private enum QualificationChange {
        NO_CHANGE,
        TO_THIS,
        TO_CLASS,
        ERROR
    }

    private Map<Def,QualificationChange> fQualificationChanges;

    private RefactoringStatus checkForCollisions() {
        // populate fQualificationChanges, return error status if any is QualificationChange.ERROR
        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange root = new CompositeChange(MOVE_REFACTORING_NAME);
        TextFileChange tfc = new TextFileChange("Move declarations", fSourceFile);

        tfc.setEdit(new MultiTextEdit());

        removeOldDeclChange(tfc, root);
        addNewDeclChange(root);
        createRefChanges(root);
        root.add(tfc);

        fFinalChange= root;
        return fFinalChange;
    }

    private void removeOldDeclChange(TextFileChange tfc, CompositeChange root) {
        TextEditGroup group= new TextEditGroup("Remove old declarations");
        for(Def def: fNodesToDefs.values()) {
            int startOffset= def.position().offset();
            int endOffset= def.position().endOffset();
            DeleteEdit edit= new DeleteEdit(startOffset, endOffset - startOffset + 1);
    
            group.addTextEdit(edit);
        }
        tfc.addTextEditGroup(group);
    }

    private void createRefChanges(CompositeChange root) {
        // Take into account info in fQualificationChanges
    }

    private void addNewDeclChange(CompositeChange root) {
        // Take into account info in fVisibilityChanges
    }
}
