package x10dt.refactoring.wizards;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import x10dt.refactoring.LoopFlatParallelizationRefactoring;

public class LoopFlatParallelizationWizard extends RefactoringWizard {
    public LoopFlatParallelizationWizard(LoopFlatParallelizationRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        LoopFlatParallelizationInputPage page= new LoopFlatParallelizationInputPage(getRefactoring().getName());

        addPage(page);
    }

    public LoopFlatParallelizationRefactoring getLoopFlatParallelizationRefactoring() {
        return (LoopFlatParallelizationRefactoring) getRefactoring();
    }
}
