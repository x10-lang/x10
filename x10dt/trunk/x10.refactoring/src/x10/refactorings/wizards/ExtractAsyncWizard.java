package x10.refactorings.wizards;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import x10.refactorings.ExtractAsyncRefactoring;

public class ExtractAsyncWizard extends RefactoringWizard {
    private static final String EXTRACT_ASYNC_WIZARD_NAME = "Extract Async";

    public ExtractAsyncWizard(ExtractAsyncRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(EXTRACT_ASYNC_WIZARD_NAME);
    }

    protected void addUserInputPages() {
        ExtractAsyncInputPage page = new ExtractAsyncInputPage(EXTRACT_ASYNC_WIZARD_NAME);

        addPage(page);
    }

    public ExtractAsyncRefactoring getExtractAsyncRefactoring() {
        return (ExtractAsyncRefactoring) getRefactoring();
    }
}
