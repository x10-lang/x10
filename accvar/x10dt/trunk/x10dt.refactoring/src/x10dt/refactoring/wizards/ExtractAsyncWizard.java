package x10dt.refactoring.wizards;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import x10dt.refactoring.X10RefactoringBase;

public class ExtractAsyncWizard extends RefactoringWizard {
    public ExtractAsyncWizard(X10RefactoringBase refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        ExtractAsyncInputPage page= new ExtractAsyncInputPage(getRefactoring().getName());

        addPage(page);
    }

    public X10RefactoringBase getExtractAsyncRefactoring() {
        return (X10RefactoringBase) getRefactoring();
    }

}
