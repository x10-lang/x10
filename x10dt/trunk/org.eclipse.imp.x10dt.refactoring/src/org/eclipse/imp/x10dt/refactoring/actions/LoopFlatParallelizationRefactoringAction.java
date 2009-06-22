package org.eclipse.imp.x10dt.refactoring.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.imp.x10dt.refactoring.LoopFlatParallelizationRefactoring;
import org.eclipse.imp.x10dt.refactoring.wizards.LoopFlatParallelizationWizard;
import org.eclipse.ui.texteditor.TextEditorAction;


public class LoopFlatParallelizationRefactoringAction extends TextEditorAction {
    static ResourceBundle ResBundle = ResourceBundle.getBundle("org.eclipse.imp.x10dt.refactoring.X10RefactoringMessages");

    public LoopFlatParallelizationRefactoringAction(UniversalEditor editor) {
        super(ResBundle, "loopFlat.", editor);
    }

    public void run() {
        final LoopFlatParallelizationRefactoring refactoring = new LoopFlatParallelizationRefactoring((UniversalEditor) this.getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new LoopFlatParallelizationWizard(refactoring, "Loop Flat Parallelization"),
                    this.getTextEditor().getSite().getShell(), "Loop Flat Parallelization", false);
    }
}
