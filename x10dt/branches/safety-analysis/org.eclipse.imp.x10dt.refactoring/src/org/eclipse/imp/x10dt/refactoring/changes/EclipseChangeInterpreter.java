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

import java.io.StringWriter;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.x10dt.refactoring.transforms.Inliner;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * Interprets an org.eclipse.imp.x10dt.refactoring.changes.Change object to produce
 * an Eclipse LTK Change object, for use with the refactoring framework.
 */
public class EclipseChangeInterpreter extends ChangeInterpreter {
    private IWorkspace fWorkspace;

    private NodeFactory fNodeFactory;

    private TypeSystem fTypeSystem;

    private Stack<org.eclipse.ltk.core.refactoring.CompositeChange> fChangeStack= new Stack<org.eclipse.ltk.core.refactoring.CompositeChange>();

    /**
     * the change for the current source file being rewritten
     */
    private TextFileChange fTextFileChange;

    /**
     * the current source file being rewritten
     */
    private SourceFile fSourceFile;

    /**
     * the top-level change that results from the rewriting
     */
    private org.eclipse.ltk.core.refactoring.Change fResult;

    private final String fLineTerminator;

    private final Compiler fCompiler;

    public EclipseChangeInterpreter(IWorkspace ws, polyglot.frontend.Compiler compiler, NodeFactory nf, TypeSystem ts, String lineTerm) {
        fWorkspace= ws;
        fCompiler= compiler;
        fNodeFactory= nf;
        fTypeSystem= ts;
        fLineTerminator= lineTerm;
    }

    public org.eclipse.ltk.core.refactoring.Change getResult() {
        return fResult;
    }

    @Override
    protected void beginTopLevelChange(Change change) {
        if (Globals.Compiler() == null) {
            Globals.initialize(fCompiler); // in case this gets called from another thread than the one that did the initial analysis
        }
        fChangeStack.push(new org.eclipse.ltk.core.refactoring.CompositeChange(change.getName()));
    }

    @Override
    protected void endTopLevelChange(Change change) {
        fResult= fChangeStack.pop();
    }

    @Override
    protected void beginFileChange(FileChange fc) {
        fTextFileChange= new TextFileChange(fc.getName(), fWorkspace.getRoot().getFile(new Path(fc.getFilePath())));
        fTextFileChange.setEdit(new MultiTextEdit());
        fChangeStack.peek().add(fTextFileChange);
    }

    @Override
    protected void endFileChange(FileChange fc) {
        fTextFileChange= null;
    }

    @Override
    protected void beginComposite(CompositeChange comp) {
        fChangeStack.push(new org.eclipse.ltk.core.refactoring.CompositeChange(comp.getName()));
    }

    @Override
    protected void endComposite(CompositeChange comp) {
        fChangeStack.pop();
    }

    @Override
    protected void performAddAnnotation(AddAnnotationChange addAnno) {
        String ann= addAnno.getAnnotation();
        Node n= addAnno.getNode();

        addTextEdit(new InsertEdit(n.position().offset(), ann));
    }

    @Override
    protected void performCopyStatement(CopyStatementChange copyStatement) {
        Stmt stmt= copyStatement.getStmt();
        Node parent= copyStatement.getStmtParent();
        int idx= copyStatement.getPos();
        int offset;
        if (parent instanceof Block) {
            Block block= (Block) parent;
            List<Stmt> blockStmts= block.statements();
            if (idx >= blockStmts.size()) {
                offset= blockStmts.get(blockStmts.size()-1).position().endOffset();
            } else {
                offset= blockStmts.get(idx).position().offset();
            }
        } else {
            throw new IllegalArgumentException("Don't know how to copy a statement into a " + parent.getClass());
        }
        addTextEdit(new InsertEdit(offset, toString(stmt) + fLineTerminator));
    }

    @Override
    protected void performDeleteActualArgument(DeleteActualArgumentChange argChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performDeleteFormalArgument(DeleteFormalArgumentChange argChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performDeleteLocalDecl(DeleteLocalDeclChange varChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performDeleteMember(DeleteMemberChange memberChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performDeleteStatement(DeleteStatementChange change) {
        Position pos= change.getStmt().position();
        addTextEdit(new DeleteEdit(pos.offset(), pos.endOffset() - pos.offset() + 1));
    }

    @Override
    protected void performInlineCall(InlineCallChange change) {
        Call call= change.getCall();
        Inliner inliner= new Inliner(call, change.getCallOwner(), change.getSourceFile(), fNodeFactory, fTypeSystem);
        Node inlinedNode= inliner.perform(change.getSimplify());

        int start= call.position().offset();
        int end= call.position().endOffset();
        String newText= toString(inlinedNode);

        addTextEdit(new ReplaceEdit(start, end - start + 1, newText));
    }

    private String toString(Node n) {
        StringWriter sw= new StringWriter();
        n.prettyPrint(sw);
        return sw.toString();
    }

    private void addTextEdit(TextEdit edit) {
        if (fTextFileChange == null) {
            throw new IllegalStateException("Null TextFileChange found in call to EclipseChangeInterpreter.addTextEdit().");
        }
        fTextFileChange.addEdit(edit);
    }

    @Override
    protected void performInsertActualArgument(InsertActualArgumentChange argChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performInsertFormalArgument(InsertFormalArgumentChange argChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performInsertExpr(InsertExprChange exprChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performInsertMember(InsertMemberChange memberChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performInsertStatement(InsertStatementChange stmtChange) {
        Stmt stmt= stmtChange.getStmt();
        Node parent= stmtChange.getStmtParent();
        int idx= stmtChange.getPos();
        int offset;
        if (parent instanceof Block) {
            Block block= (Block) parent;
            List<Stmt> blockStmts= block.statements();
            if (idx >= blockStmts.size()) {
                offset= blockStmts.get(blockStmts.size()-1).position().endOffset();
            } else {
                offset= blockStmts.get(idx).position().offset();
            }
        } else {
            throw new IllegalArgumentException("Don't know how to copy a statement into a " + parent.getClass());
        }
        addTextEdit(new InsertEdit(offset, stmt.toString() + fLineTerminator));
    }

    @Override
    protected void performInsertLocalDecl(InsertLocalDeclChange varChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performModifyChange(ModifyChange modifyChange) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void performReplaceExpr(ReplaceExpressionChange exprChange) {
        Expr oldExpr= exprChange.getOldExpr();
        Expr newExpr= exprChange.getNewExpr();
        Position pos= oldExpr.position();

        addTextEdit(new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, toString(newExpr)));
    }

    @Override
    protected void performReplaceStatement(ReplaceStatementChange stmtChange) {
        Stmt oldStmt= stmtChange.getOldStmt();
        Stmt newStmt= stmtChange.getNewStmt();
        Position pos= oldStmt.position();

        addTextEdit(new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, toString(newStmt)));
    }

    @Override
    protected void performSubstitutionChange(SubstitutionChange substitutionChange) {
        // TODO Auto-generated method stub
    }
}
