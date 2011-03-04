package x10dt.refactoring;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Position;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Block;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import x10.ast.X10MethodDecl;
import x10.effects.constraints.Effect;
import x10dt.refactoring.actions.MarkContextAction;
import x10dt.refactoring.analysis.ReachingDefsVisitor;
import x10dt.refactoring.effects.EffectsVisitor;
import x10dt.refactoring.utils.NodePathComputer;

public class ExtractAsyncRefactoring extends X10RefactoringBase {
    private static final String EXTRACT_ASYNC_REFACTORING_NAME= "Extract Async";

    private NodePathComputer fPathComputer;

    private List<Node> fContextNodes;

    public ExtractAsyncRefactoring(ITextEditor editor) {
        super(editor);
    }

    @Override
    public String getName() {
        return EXTRACT_ASYNC_REFACTORING_NAME;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors");
        }
        Position contextPos= MarkContextAction.getCurrentContext((ITextEditor) fEditor);

        MarkContextAction.clearCurrentContext((ITextEditor) fEditor);

        if (contextPos == null) {
            return RefactoringStatus.createFatalErrorStatus("You must first select the finish context with 'Mark Finish Context'.");
        }
        if (!containsAll(contextPos, fSelNodes)) {
            return RefactoringStatus.createFatalErrorStatus("The finish context must contain the code to be extracted to an 'async'.");
        }

        Node anySelNode= fSelNodes.get(0);
        fPathComputer= new NodePathComputer(fSourceAST, anySelNode);
        fContainingMethod= (X10MethodDecl) fPathComputer.findEnclosingNode(anySelNode, MethodDecl.class);

        Node contextNode= (Node) fNodeLocator.findNode(fSourceAST, contextPos.offset, contextPos.offset + contextPos.length-1);

        if (contextNode == null || !(contextNode instanceof Stmt || contextNode instanceof Block)) {
            return RefactoringStatus.createFatalErrorStatus("The selected context must be a set of statements or a block.");
        }

        fContextNodes= findContainedNodes(contextNode, contextPos.offset, contextPos.offset + contextPos.length - 1);

        return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        try {
            List<Node> followingContextNodes= extractFollowingContext(fSelNodes, fContextNodes);
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
                return RefactoringStatus.createErrorStatus("The selected code contains effects that don't commute with the surrounding context.");
            }
            return RefactoringStatus.create(new Status(IStatus.OK, X10DTRefactoringPlugin.kPluginID, ""));
        } catch (Exception e) {
            return RefactoringStatus.createFatalErrorStatus("An exception occurred while analyzing the selected code: " + e.getMessage());
        }
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange outerChange = new CompositeChange(EXTRACT_ASYNC_REFACTORING_NAME);
        TextFileChange tfc = new TextFileChange("Wrap statements in 'async'", fSourceFile);

        tfc.setEdit(new MultiTextEdit());

        createAddAsyncChange(tfc, fSelNodes);
        createAddFinishChange(tfc, fContextNodes);
        outerChange.add(tfc);

        fFinalChange= outerChange;
        return fFinalChange;
    }
}
