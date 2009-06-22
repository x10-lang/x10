package x10.refactorings.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.TextEditorAction;

import x10.refactorings.ExtractAsyncRefactoring;
import x10.refactorings.wizards.ExtractAsyncWizard;

public class ExtractAsyncRefactoringAction extends TextEditorAction {
    // private final UniversalEditor fEditor;

    static ResourceBundle ResBundle = ResourceBundle.getBundle("x10.refactorings.RefactoringMessages");

    public ExtractAsyncRefactoringAction(UniversalEditor editor) {
        super(ResBundle, "extractConcurrent.", editor);
        // fEditor= editor;
    }

    public void run() {
        final ExtractAsyncRefactoring refactoring = new ExtractAsyncRefactoring((UniversalEditor) this.getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new ExtractAsyncWizard(refactoring, "Extract Concurrent"),
                    this.getTextEditor().getSite().getShell(), "Extract Concurrent", false);
    }
}
