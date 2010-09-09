package x10.refactorings.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import x10.refactorings.ExtractAsyncRefactoring;
import x10.refactorings.wizards.ExtractAsyncWizard;

public class ExtractAsyncRefactoringAction extends TextEditorAction {
    static ResourceBundle ResBundle = ResourceBundle.getBundle("x10.refactorings.RefactoringMessages");

    public ExtractAsyncRefactoringAction(ITextEditor editor) {
        super(ResBundle, "extractAsync.", editor);
    }

    public void run() {
        final ExtractAsyncRefactoring refactoring = new ExtractAsyncRefactoring(getTextEditor());

        if (refactoring != null) {
            new RefactoringStarter().activate(refactoring, new ExtractAsyncWizard(refactoring),
                    getTextEditor().getSite().getShell(), refactoring.getName(), false);
        }
    }
}
