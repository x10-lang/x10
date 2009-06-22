package x10.refactorings;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.ui.parser.CompilerDelegate;
import org.eclipse.imp.x10dt.ui.parser.ExtensionInfo;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.TextEditor;

import polyglot.ast.Block;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerms;
import x10.effects.constraints.Effect;
import x10.refactorings.utils.NodePathComputer;

public class LoopFlatParallelizationRefactoring extends Refactoring {
    /**
     * The source file from which the refactoring was initiated
     */
    private final IFile fSourceFile;

    private final SourceFile fSourceAST;

    /**
     * The editor from which the refactoring was initiated
     */
    private IASTFindReplaceTarget fEditor;

    /**
     * The user-selected AST node
     */
    private final Node fNode;

    /**
     * The user-selected ForLoop (often the same as fNode, if the latter is a ForLoop)
     */
    private ForLoop fLoop;

    /**
     * The source change we're building
     */
    private Change fFinalChange;

    private MessageConsoleStream fConsoleStream;

    private X10MethodDecl fMethod;

    public LoopFlatParallelizationRefactoring(TextEditor editor) {
        fEditor = (IASTFindReplaceTarget) editor;

        IEditorInput input = editor.getEditorInput();
        IParseController parseController = fEditor.getParseController();
        fSourceAST= (SourceFile) parseController.getCurrentAst();

        if (input instanceof IFileEditorInput) {
            IFileEditorInput fileInput = (IFileEditorInput) input;

            fSourceFile = fileInput.getFile();
            fNode = findNode();
        } else {
            fSourceFile = null;
            fNode = null;
        }
        fConsoleStream = X10DTCorePlugin.getInstance().getConsole().newMessageStream();
    }

    /**
     * @return the innermost AST node that contains the entire editor selection,
     * after trimming any surrounding whitespace
     */
    public Node findNode() {
        Point sel = trimSelection();
        ISourcePositionLocator locator = fEditor.getParseController().getSourcePositionLocator();

        return (Node) locator.findNode(fSourceAST, sel.x, sel.x + sel.y - 1);
    }

    /**
     * Remove whitespace from beginning and end of the current editor selection,
     * and return the resulting region, expressed as a Point, where the x coordinate
     * is the starting offset of the selection, and the y coordinate is its length.
     */
    private Point trimSelection() {
        Point sel= fEditor.getSelection();
        String text= fEditor.getSelectionText();
        int start= sel.x;
        int end= sel.x + sel.y - 1;
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

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors");
        }
        if (!(fNode instanceof ForLoop)) {
            return RefactoringStatus.createFatalErrorStatus("Selected node is not a loop");
        }
        fLoop= (ForLoop) fNode;
        NodePathComputer pathComputer= new NodePathComputer(fSourceAST, fLoop);
        MethodDecl md= findEnclosingNode(fLoop, pathComputer.getPath(), MethodDecl.class);

        fMethod= (X10MethodDecl) md;

        if (loopHasAsync(fLoop)) {
            return RefactoringStatus.createFatalErrorStatus("Body of selected loop is already wrapped in an async");
        }

        return RefactoringStatus.create(new Status(IStatus.OK, X10RefactoringPlugin.kPluginID, ""));
    }

    /**
     * Finds the innermost Node of the given type that encloses the given Node
     * @param loop
     * @param path
     */
    private <M> M findEnclosingNode(Node node, List<Node> path, Class<M> clazz) {
        int i= path.size()-1;
        for(; i >= 0; i--) {
            Node pathNode = path.get(i);
            if (pathNode == node) {
                break;
            }
        }
        for(; i >= 0; i--) {
            Node pathNode = path.get(i);
            if (clazz.isInstance(pathNode)) {
                return (M) pathNode;
            }
        }
        return null;
    }

    private boolean loopHasAsync(ForLoop loop) {
        Stmt loopBody= loop.body();

        if (loopBody instanceof Async) {
            return true;
        }
        if (loopBody instanceof Block) {
            Block block = (Block) loopBody;
            List<Stmt> blockStmts = block.statements();

            if (blockStmts.size() == 1 && blockStmts.get(0) instanceof Async) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            IParseController pc= fEditor.getParseController();
            CompilerDelegate cd= ((ParseController) pc).getCompiler();
            ExtensionInfo extInfo = cd.getExtInfo();
            TypeSystem ts= extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            Stmt loopBody = fLoop.body();

            ReachingDefsVisitor rdVisitor = new ReachingDefsVisitor(fMethod, null, ts, nf);
            fMethod.visit(rdVisitor);

            EffectsVisitor effVisitor= new EffectsVisitor(rdVisitor.getReachingDefs(), fMethod);
            loopBody.visit(effVisitor);
            effVisitor.dump();
            Effect bodyEff= effVisitor.getEffectFor(loopBody);

            if (bodyEff == null) {
                return RefactoringStatus.createFatalErrorStatus("Unable to compute the effects of the loop body.");
            }
            if (!bodyEff.commutesWithForall(XTerms.makeLocal(new XNameWrapper<Id>(fLoop.formal().name())))) {
                return RefactoringStatus.createErrorStatus("The loop body contains effects that don't commute.");
            }
            return RefactoringStatus.create(new Status(IStatus.OK, X10RefactoringPlugin.kPluginID, ""));
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus(e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange outerChange = new CompositeChange("Loop Flat Parallelization");
        int asyncOffset = fLoop.body().position().offset();
        int blockEnd = fNode.position().endOffset();

        TextFileChange tfc = new TextFileChange("Loop Flat Parallelization", fSourceFile);
        tfc.setEdit(new MultiTextEdit());
        tfc.addEdit(new InsertEdit(asyncOffset, "async "));

        outerChange.add(tfc);
        fFinalChange= outerChange;
        return fFinalChange;
    }

    @Override
    public String getName() {
        return "Loop Flat Parallelization";
    }
}
