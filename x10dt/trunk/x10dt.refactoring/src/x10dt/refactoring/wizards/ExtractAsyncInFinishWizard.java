package x10dt.refactoring.wizards;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import x10dt.refactoring.ExtractAsyncInFinishRefactoring;

public class ExtractAsyncInFinishWizard extends RefactoringWizard {
    public ExtractAsyncInFinishWizard(ExtractAsyncInFinishRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        ExtractAsyncInFinishInputPage page= new ExtractAsyncInFinishInputPage(getRefactoring().getName());

        addPage(page);
    }

    public ExtractAsyncInFinishRefactoring getExtractAsyncInFinishRefactoring() {
        return (ExtractAsyncInFinishRefactoring) getRefactoring();
    }

}
