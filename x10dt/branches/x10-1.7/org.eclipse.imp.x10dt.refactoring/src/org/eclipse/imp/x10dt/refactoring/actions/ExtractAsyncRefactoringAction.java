package org.eclipse.imp.x10dt.refactoring.actions;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.imp.x10dt.refactoring.ExtractAsyncRefactoring;
import org.eclipse.imp.x10dt.refactoring.X10RefactoringBase;
import org.eclipse.imp.x10dt.refactoring.wizards.ExtractAsyncWizard;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;


public class ExtractAsyncRefactoringAction extends TextEditorAction {
    public ExtractAsyncRefactoringAction(ITextEditor editor) {
        super(X10RefactoringMessages.ResBundle, "extractAsync.", editor);
    }

    public void run() {
        final X10RefactoringBase refactoring = new ExtractAsyncRefactoring(getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new ExtractAsyncWizard(refactoring),
                    getTextEditor().getSite().getShell(), refactoring.getName(), false);
    }
}
