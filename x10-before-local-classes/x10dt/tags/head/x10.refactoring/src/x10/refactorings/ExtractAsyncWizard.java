package x10.refactorings;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ExtractAsyncWizard extends RefactoringWizard {
    public ExtractAsyncWizard(ExtractAsyncRefactoring refactoring, String pageTitle) {
	super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
	setDefaultPageTitle(pageTitle);
    }

    protected void addUserInputPages() {
	ExtractAsyncInputPage page= new ExtractAsyncInputPage("Extract Async");

	addPage(page);
    }

    public ExtractAsyncRefactoring getExtractAsyncRefactoring() {
	return (ExtractAsyncRefactoring) getRefactoring();
    }
}
