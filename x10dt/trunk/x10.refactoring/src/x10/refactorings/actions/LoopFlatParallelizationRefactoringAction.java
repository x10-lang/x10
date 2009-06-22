package x10.refactorings.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.TextEditorAction;

import x10.refactorings.LoopFlatParallelizationRefactoring;
import x10.refactorings.wizards.LoopFlatParallelizationWizard;

public class LoopFlatParallelizationRefactoringAction extends TextEditorAction {
    static ResourceBundle ResBundle = ResourceBundle.getBundle("x10.refactorings.RefactoringMessages");

    public LoopFlatParallelizationRefactoringAction(UniversalEditor editor) {
        super(ResBundle, "loopFlat.", editor);
        // fEditor= editor;
    }

    public void run() {
        final LoopFlatParallelizationRefactoring refactoring = new LoopFlatParallelizationRefactoring((UniversalEditor) this.getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new LoopFlatParallelizationWizard(refactoring, "Loop Flat Parallelization"),
                    this.getTextEditor().getSite().getShell(), "Extract Concurrent", false);
    }
}
