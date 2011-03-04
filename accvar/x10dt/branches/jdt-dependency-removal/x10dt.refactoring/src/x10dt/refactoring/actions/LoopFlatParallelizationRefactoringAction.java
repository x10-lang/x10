package x10dt.refactoring.actions;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import x10dt.refactoring.LoopFlatParallelizationRefactoring;
import x10dt.refactoring.wizards.LoopFlatParallelizationWizard;

public class LoopFlatParallelizationRefactoringAction extends TextEditorAction {
    public LoopFlatParallelizationRefactoringAction(ITextEditor editor) {
        super(X10RefactoringMessages.ResBundle, "loopFlat.", editor);
    }

    public void run() {
        final LoopFlatParallelizationRefactoring refactoring = new LoopFlatParallelizationRefactoring(getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new LoopFlatParallelizationWizard(refactoring),
                    getTextEditor().getSite().getShell(), refactoring.getName(), false);
    }
}
