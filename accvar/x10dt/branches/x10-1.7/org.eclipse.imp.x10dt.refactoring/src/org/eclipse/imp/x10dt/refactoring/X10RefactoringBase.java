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

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.imp.x10dt.refactoring.changes.EclipseChangeInterpreter;
import org.eclipse.imp.x10dt.refactoring.utils.NodePathComputer;
import org.eclipse.imp.x10dt.ui.parser.CompilerDelegate;
import org.eclipse.imp.x10dt.ui.parser.ExtensionInfo;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.FileStatusContext;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Block;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Compiler;

public abstract class X10RefactoringBase extends Refactoring {
    /**
     * The source file from which the refactoring was initiated
     */
    protected final IFile fSourceFile;

    protected final SourceFile fSourceAST;

    /**
     * The editor from which the refactoring was initiated
     */
    protected ITextEditor fEditor;

    protected ISourcePositionLocator fNodeLocator;

    /**
     * The user-selected AST nodes
     */
    protected final List<Node> fSelNodes;

    protected X10MethodDecl fContainingMethod;

    protected NodePathComputer fPathComputer;

    /**
     * The source change we're building
     */
    protected Change fFinalChange;

    protected boolean fVerbose;

    protected PrintStream fConsoleStream;

    private String fLineTerminator;

    protected X10NodeFactory fNodeFactory;

    protected X10TypeSystem fTypeSystem;

    protected Compiler fCompiler;

    protected X10RefactoringBase(ITextEditor editor) {
        fEditor= editor;

        IEditorInput input= editor.getEditorInput();
        IParseController parseController= ((IASTFindReplaceTarget) fEditor).getParseController();

        fNodeLocator= parseController.getSourcePositionLocator();
        fSourceAST= (SourceFile) parseController.getCurrentAst();

        if (input instanceof IFileEditorInput) {
            IFileEditorInput fileInput= (IFileEditorInput) input;

            fSourceFile= fileInput.getFile();
            fSelNodes= determineSelectedNodes();
        } else {
            // TODO How to handle case where we can't get an IFile?
            // Various sub-classes ultimately create a TextFileChange, which needs an IFile...
            fSelNodes= null;
            fSourceFile= null;
        }
        fConsoleStream= X10DTRefactoringPlugin.getInstance().getConsoleStream();
    }

    public void setVerbose(boolean verbose) {
        fVerbose= verbose;
    }

    /**
     * @return the list of AST nodes that contain the entire editor selection,
     * after trimming any surrounding whitespace. If the selection contains the
     * entirety of the innermost enclosing AST node, that node is returned;
     * otherwise, the set of nodes contained within the selection is returned.
     */
    public List<Node> determineSelectedNodes() {
        Point sel= trimSelection();
        int selOffset= sel.x;
        int selEnd= sel.x + sel.y - 1;
        Node containingNode= (Node) fNodeLocator.findNode(fSourceAST, selOffset, selEnd);

        return findContainedNodes(containingNode, selOffset, selEnd);
    }

    /**
     * 
     * @param selOffset
     * @param selEnd
     * @param result
     */
    protected List<Node> findContainedNodes(Node containingNode, int selOffset, int selEnd) {
        List<Node> result= new LinkedList<Node>();

        // TODO Handle (as an error) the case where the given extent does not contain complete statements
        if (containingNode instanceof Block) {
            Block block= (Block) containingNode;
            List<Stmt> stmts= block.statements();

            for(Stmt s: stmts) {
                if (contains(selOffset, selEnd, s)) {
                    result.add(s);
                }
            }
        } else {
            result.add(containingNode);
        }
        return result;
    }

    // ===========================
    // Position-related utilities
    // ===========================
    protected boolean contains(Point sel, Stmt s) {
        int offset= sel.x;
        int end= sel.x + sel.y - 1;

        return contains(offset, end, s);
    }

    protected boolean contains(int offset, int end, Node n) {
        return offset <= n.position().offset() && end >= n.position().endOffset();
    }

    protected boolean containsAll(Position pos, List<Node> nodes) {
        for(Node node: nodes) {
            if (!contains(pos, node.position())) {
                return false;
            }
        }
        return true;
    }

    protected boolean contains(Position contextPos, polyglot.util.Position nodePos) {
        return (contextPos.getOffset() <= nodePos.offset() &&
            contextPos.getOffset() + contextPos.getLength() >= nodePos.endOffset());
    }

    protected boolean isAfter(Node n, int offset) {
        return n.position().offset() > offset;
    }

    /**
     * Remove whitespace from beginning and end of the current editor selection,
     * and return the resulting region, expressed as a Point, where the x coordinate
     * is the starting offset of the selection, and the y coordinate is its length.
     */
    protected Point trimSelection() {
        ITextSelection textSel= (ITextSelection) fEditor.getSelectionProvider().getSelection();
        String text= textSel.getText();
        int start= textSel.getOffset();
        int end= textSel.getOffset() + textSel.getLength() - 1;
        for(int i=0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                start++;
            } else {
                break;
            }
        }
        for(int j= text.length()-1; j >= 0; j--) {
            if (Character.isWhitespace(text.charAt(j))) {
                end--;
            } else {
                break;
            }
        }
        return new Point(start, end - start + 1);
    }

    // =======================
    // Building sets of Nodes
    // =======================
    /**
     * @return a sequential list of Nodes, each of which follow all of <code>selNodes</code>
     * and lie within <code>contextNodes</code>.
     */
    protected List<Node> extractFollowingContext(List<Node> selNodes, List<Node> contextNodes) {
        // TODO Handle case where the selNodes lie inside one of the contextNodes
        List<Node> result= new LinkedList<Node>();
        int lastSelNodeEnd= selNodes.get(selNodes.size()-1).position().endOffset();

        for(Node contextNode: contextNodes) {
            if (!selNodes.contains(contextNode) && isAfter(contextNode, lastSelNodeEnd)) {
                result.add(contextNode);
            }
        }
        return result;
    }

    // =========================
    // Change-related utilities
    // =========================
    protected void createAddAsyncChange(TextFileChange tfc, List<Node> nodesToWrap) {
        wrapNodes(nodesToWrap, "async", tfc);
    }

    protected void createAddFinishChange(TextFileChange tfc, List<Node> nodesToWrap) {
        wrapNodes(nodesToWrap, "finish", tfc);
    }

    protected void wrapNodes(List<Node> nodesToWrap, String op, TextFileChange tfc) {
        int nodesOffset= nodesToWrap.get(0).position().offset();

        if (nodesToWrap.size() == 1) {
            tfc.addEdit(new InsertEdit(nodesOffset, op + " "));
        } else {
            int blockClose= nodesToWrap.get(nodesToWrap.size()-1).position().endOffset();

            tfc.addEdit(new InsertEdit(nodesOffset, op + " {" + getLineTerminator()));
            tfc.addEdit(new InsertEdit(blockClose, "}" + getLineTerminator()));
        }
    }

    protected String getLineTerminator() {
        if (fLineTerminator == null) {
            ITextEditor textEditor= (ITextEditor) fEditor;
            IDocument doc= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

            fLineTerminator= doc.getLegalLineDelimiters()[0];
        }
        return fLineTerminator;
    }

    private final RefactoringStatus OK_STATUS= RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));

    protected RefactoringStatus okStatus() {
        return OK_STATUS;
    }

    protected RefactoringStatus errorStatus(String msg) {
        return RefactoringStatus.createErrorStatus(msg);
    }

    protected RefactoringStatus errorStatus(String mainMsg, polyglot.util.Position pos, String msg) {
        RefactoringStatus status= RefactoringStatus.createErrorStatus(mainMsg);
        addErrorAnnotation(pos, msg, status);
        return status;
    }

    protected IFile getFileFromPosition(polyglot.util.Position pos) {
        IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
        String posFile= pos.file();
        String wsRootPath= wsRoot.getLocation().toOSString();
        if (posFile.startsWith(wsRootPath)) {
            return wsRoot.getFile(new Path(posFile.substring(wsRootPath.length())));
        }
        return wsRoot.getFile(new Path(posFile));
    }

    protected void addErrorAnnotation(polyglot.util.Position pos, String msg, RefactoringStatus status) {
        IRegion region= new Region(pos.offset(), pos.endOffset() - pos.offset() + 1);
        FileStatusContext context= new FileStatusContext(getFileFromPosition(pos), region);

        status.addEntry(new RefactoringStatusEntry(RefactoringStatus.ERROR, msg, context));
    }

    protected RefactoringStatus fatalStatus(String msg) {
        return RefactoringStatus.createFatalErrorStatus(msg);
    }

    protected void getNodeFactoryTypeSystem() {
        IParseController pc= ((IASTFindReplaceTarget) fEditor).getParseController();
        CompilerDelegate cd= ((ParseController) pc).getCompiler();
        ExtensionInfo extInfo = cd.getExtInfo();

        fCompiler= cd.getCompiler();
        fNodeFactory= (X10NodeFactory) extInfo.nodeFactory();
        fTypeSystem= (X10TypeSystem) extInfo.typeSystem();
    }

    protected Change interpretChange(org.eclipse.imp.x10dt.refactoring.changes.Change change) {
        IWorkspace ws= ResourcesPlugin.getWorkspace();
        EclipseChangeInterpreter interp= new EclipseChangeInterpreter(ws, fCompiler, fNodeFactory, fTypeSystem, getLineTerminator());
    
        interp.perform(change, fSourceAST);
        return interp.getResult();
    }
}
