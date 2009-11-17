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

package org.eclipse.imp.x10dt.refactoring.changes;

import org.eclipse.imp.x10dt.refactoring.X10DTRefactoringPlugin;

import polyglot.ast.SourceFile;

public abstract class ChangeInterpreter {
    protected abstract void beginTopLevelChange(Change change);

    protected abstract void endTopLevelChange(Change change);

    protected abstract void beginFileChange(FileChange fc);

    protected abstract void endFileChange(FileChange fc);

    protected abstract void beginComposite(CompositeChange comp);

    protected abstract void endComposite(CompositeChange comp);

    protected abstract void performAddAnnotation(AddAnnotationChange addAnno);

    protected abstract void performCopyStatement(CopyStatementChange copyStatement);

    protected abstract void performDeleteActualArgument(DeleteActualArgumentChange argChange);

    protected abstract void performDeleteFormalArgument(DeleteFormalArgumentChange argChange);

    protected abstract void performDeleteLocalDecl(DeleteLocalDeclChange varChange);

    protected abstract void performDeleteMember(DeleteMemberChange memberChange);

    protected abstract void performDeleteStatement(DeleteStatementChange change);

    protected abstract void performInlineCall(InlineCallChange change);

    protected abstract void performInsertActualArgument(InsertActualArgumentChange argChange);

    protected abstract void performInsertExpr(InsertExprChange exprChange);

    protected abstract void performInsertFormalArgument(InsertFormalArgumentChange argChange);

    protected abstract void performInsertLocalDecl(InsertLocalDeclChange varChange);

    protected abstract void performInsertMember(InsertMemberChange memberChange);

    protected abstract void performInsertStatement(InsertStatementChange statementChange);

    protected abstract void performModifyChange(ModifyChange modifyChange);

    protected abstract void performReplaceExpr(ReplaceExpressionChange exprChange);

    protected abstract void performReplaceStatement(ReplaceStatementChange stmtChange);

    protected abstract void performSubstitutionChange(SubstitutionChange substitutionChange);

    public void perform(Change change, SourceFile srcFile) {
        beginTopLevelChange(change);
        dispatch(change);
        endTopLevelChange(change);
    }

    private void dispatch(Change change) {
        if (change instanceof FileChange) {
            FileChange fc= (FileChange) change;

            beginFileChange(fc);
            performComposite(fc);
            endFileChange(fc);

        } else if (change instanceof CompositeChange) {
            performComposite((CompositeChange) change);

        } else if (change instanceof CopyStatementChange) {
            performCopyStatement((CopyStatementChange) change);

        } else if (change instanceof DeleteFormalArgumentChange) {
            performDeleteFormalArgument((DeleteFormalArgumentChange) change);

        } else if (change instanceof DeleteLocalDeclChange) {
            performDeleteLocalDecl((DeleteLocalDeclChange) change);

        } else if (change instanceof DeleteMemberChange) {
            performDeleteMember((DeleteMemberChange) change);

        } else if (change instanceof DeleteStatementChange) {
            performDeleteStatement((DeleteStatementChange) change);

        } else if (change instanceof InlineCallChange) {
            performInlineCall((InlineCallChange) change);

        } else if (change instanceof InsertExprChange) {
            performInsertExpr((InsertExprChange) change);

        } else if (change instanceof InsertFormalArgumentChange) {
            performInsertFormalArgument((InsertFormalArgumentChange) change);

        } else if (change instanceof InsertLocalDeclChange) {
            performInsertLocalDecl((InsertLocalDeclChange) change);

        } else if (change instanceof InsertMemberChange) {
            performInsertMember((InsertMemberChange) change);

        } else if (change instanceof InsertStatementChange) {
            performInsertStatement((InsertStatementChange) change);

        } else if (change instanceof ModifyChange) {
            performModifyChange((ModifyChange) change);

        } else if (change instanceof ReplaceExpressionChange) {
            performReplaceExpr((ReplaceExpressionChange) change);

        } else if (change instanceof ReplaceStatementChange) {
            performReplaceStatement((ReplaceStatementChange) change);

        } else if (change instanceof SubstitutionChange) {
            performSubstitutionChange((SubstitutionChange) change);
        } else {
            X10DTRefactoringPlugin.getInstance().writeErrorMsg("Unhandled change type: " + change.getClass().getName());
        }
    }

    private void performComposite(CompositeChange compositeChange) {
        beginComposite(compositeChange);
        for(Change child: compositeChange.getChildren()) {
            dispatch(child);
        }
        endComposite(compositeChange);
    }
}
