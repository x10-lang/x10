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

/**
 * Interprets a Change object to rewrite a set of Polyglot ASTs.<br>
 * The resulting ASTs <em>can</em> be serialized, but typically they're subjected to
 * more optimization and then, code generation.
 */
public class PolyglotChangeInterpreter extends ChangeInterpreter {
    @Override
    protected void beginTopLevelChange(Change change) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void endTopLevelChange(Change change) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void beginFileChange(FileChange fc) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void endFileChange(FileChange fc) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void beginComposite(CompositeChange comp) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void endComposite(CompositeChange comp) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performAddAnnotation(AddAnnotationChange addAnno) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performCopyStatement(CopyStatementChange copyStatement) {
        throw new UnsupportedOperationException("copy statement");
    }

    @Override
    protected void performDeleteActualArgument(DeleteActualArgumentChange argChange) {
        throw new UnsupportedOperationException("delete actual arg");
    }

    @Override
    protected void performDeleteFormalArgument(DeleteFormalArgumentChange argChange) {
        throw new UnsupportedOperationException("delete formal arg");
    }

    @Override
    protected void performDeleteLocalDecl(DeleteLocalDeclChange varChange) {
        throw new UnsupportedOperationException("delete local decl");
    }

    @Override
    protected void performDeleteMember(DeleteMemberChange memberChange) {
        throw new UnsupportedOperationException("delete member");
    }

    @Override
    protected void performDeleteStatement(DeleteStatementChange change) {
        throw new UnsupportedOperationException("delete statement");
    }

    @Override
    protected void performInlineCall(InlineCallChange change) {
        throw new UnsupportedOperationException("inline call");
    }

    @Override
    protected void performInsertActualArgument(InsertActualArgumentChange argChange) {
        throw new UnsupportedOperationException("insert actual arg");
    }

    @Override
    protected void performInsertFormalArgument(InsertFormalArgumentChange argChange) {
        throw new UnsupportedOperationException("insert formal arg");
    }

    @Override
    protected void performInsertExpr(InsertExprChange exprChange) {
        throw new UnsupportedOperationException("insert expr");
    }

    @Override
    protected void performInsertMember(InsertMemberChange memberChange) {
        throw new UnsupportedOperationException("insert member");
    }

    @Override
    protected void performInsertStatement(InsertStatementChange statementChange) {
        throw new UnsupportedOperationException("insert statement");
    }

    @Override
    protected void performInsertLocalDecl(InsertLocalDeclChange varChange) {
        throw new UnsupportedOperationException("insert local decl");
    }

    @Override
    protected void performModifyChange(ModifyChange modifyChange) {
        throw new UnsupportedOperationException("modify");
    }

    @Override
    protected void performReplaceExpr(ReplaceExpressionChange exprChange) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void performReplaceStatement(ReplaceStatementChange stmtChange) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void performSubstitutionChange(SubstitutionChange substitutionChange) {
        throw new UnsupportedOperationException("substitution");
    }
}
