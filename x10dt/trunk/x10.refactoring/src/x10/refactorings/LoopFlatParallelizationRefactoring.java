package x10.refactorings;

import java.io.PrintStream;
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
import org.eclipse.ui.editors.text.TextEditor;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import x10.constraint.XLocal;
import x10.constraint.XTerms;
import x10.effects.constraints.Effect;
import x10.refactorings.EffectsVisitor.XVarDefWrapper;
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

    private X10MethodDecl fMethod;

    private PrintStream fConsoleStream;

    private NodePathComputer fPathComputer;

    private List<Node> fNodePath;

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
        fConsoleStream = X10RefactoringPlugin.getInstance().getConsoleStream();
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
        fPathComputer= new NodePathComputer(fSourceAST, fLoop);
        fNodePath = fPathComputer.getPath();
        MethodDecl md= fPathComputer.findEnclosingNode(fLoop, MethodDecl.class);

        fMethod= (X10MethodDecl) md;

        if (loopHasAsync(fLoop)) {
            return RefactoringStatus.createFatalErrorStatus("Body of selected loop is already wrapped in an async");
        }

        return RefactoringStatus.create(new Status(IStatus.OK, X10RefactoringPlugin.kPluginID, ""));
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

    private boolean loopIsWrappedWithFinish() {
        Node loopParent= fPathComputer.getParent(fLoop);

        if (loopParent instanceof Finish) {
            return true;
        }

        Node loopGrandparent= fPathComputer.getParent(loopParent);

        if (loopGrandparent instanceof Finish && loopParent instanceof Block && ((Block) loopParent).statements().size() == 1) {
            return true;
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
            X10Formal loopVar = (X10Formal) fLoop.formal();
            List<Formal> explodedVars= loopVar.vars();

            ReachingDefsVisitor rdVisitor = new ReachingDefsVisitor(fMethod, null, ts, nf);
            fMethod.visit(rdVisitor);

            EffectsVisitor effVisitor= new EffectsVisitor(rdVisitor.getReachingDefs(), fMethod);
            loopBody.visit(effVisitor);
            effVisitor.dump();
            Effect bodyEff= effVisitor.getEffectFor(loopBody);

            if (bodyEff == null) {
                return RefactoringStatus.createFatalErrorStatus("Unable to compute the effects of the loop body.");
            }
            fConsoleStream.println("***");
            fConsoleStream.println("Loop body effect = " + bodyEff);
            fConsoleStream.println("Loop induction variable = " + loopVar.name());
            if (explodedVars.size() > 0) fConsoleStream.println("  exploded vars: " + explodedVars);

            // HACK If the loop formal has "exploded var syntax" (e.g. "for(p(i): Point in r) { ... }"),
            // then we should do a commutesWithForall() over the set of all the induction variables,
            // but Effect doesn't provide enough API for that yet.
            // So the following assumes that if there are exploded vars, there is actually only 1 (as
            // in the above example), and that's the one over which we want to quantify.
            VarDef loopLocalDef= (explodedVars.size() > 0) ? explodedVars.get(0).localDef() : loopVar.localDef();
            XLocal loopLocal= XTerms.makeLocal(new XVarDefWrapper(loopLocalDef));

            if (!bodyEff.commutesWithForall(loopLocal)) {
                return RefactoringStatus.createErrorStatus("The loop body contains effects that don't commute.");
            }
            return RefactoringStatus.create(new Status(IStatus.OK, X10RefactoringPlugin.kPluginID, ""));
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus("Exception occurred while analyzing loop: " + e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange outerChange = new CompositeChange("Loop Flat Parallelization");
        TextFileChange tfc = new TextFileChange("Add 'async' to loop body", fSourceFile);

        tfc.setEdit(new MultiTextEdit());

        createAddAsyncChange(tfc);

        if (!loopIsWrappedWithFinish()) {
            createAddFinishChange(tfc);
        }
        outerChange.add(tfc);

        fFinalChange= outerChange;
        return fFinalChange;
    }

    private void createAddAsyncChange(TextFileChange tfc) {
        int asyncOffset = fLoop.body().position().offset();
        tfc.addEdit(new InsertEdit(asyncOffset, "async "));
    }

    private void createAddFinishChange(TextFileChange tfc) {
        int forStart = fLoop.position().offset();
        tfc.addEdit(new InsertEdit(forStart, "finish "));
    }

    @Override
    public String getName() {
        return "Loop Flat Parallelization";
    }
}
