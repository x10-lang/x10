package org.eclipse.imp.x10dt.refactoring.wizards;

import org.eclipse.imp.x10dt.refactoring.ExtractAsyncInFinishRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

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
