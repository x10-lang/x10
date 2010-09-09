package x10dt.refactoring;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
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
import x10.ast.X10MethodDecl;
import x10dt.refactoring.utils.NodePathComputer;

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
        if (nodesToWrap.size() == 1) {
            int asyncOffset= nodesToWrap.get(0).position().offset();

            tfc.addEdit(new InsertEdit(asyncOffset, op + " "));
        } else {
            int asyncOffset= nodesToWrap.get(0).position().offset();
            int blockClose= nodesToWrap.get(nodesToWrap.size()-1).position().endOffset();

            tfc.addEdit(new InsertEdit(asyncOffset, op + " {" + fLineTerminator));
            tfc.addEdit(new InsertEdit(blockClose, "}" + fLineTerminator));
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
}
