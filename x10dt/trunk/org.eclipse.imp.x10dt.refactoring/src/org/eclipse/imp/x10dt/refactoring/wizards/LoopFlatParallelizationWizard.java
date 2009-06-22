package org.eclipse.imp.x10dt.refactoring.wizards;

import org.eclipse.imp.x10dt.refactoring.LoopFlatParallelizationRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


public class LoopFlatParallelizationWizard extends RefactoringWizard {
    public LoopFlatParallelizationWizard(LoopFlatParallelizationRefactoring refactoring, String pageTitle) {
	super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
	setDefaultPageTitle(pageTitle);
    }

    protected void addUserInputPages() {
	LoopFlatParallelizationInputPage page= new LoopFlatParallelizationInputPage("Loop Flat Parallelization");

	addPage(page);
    }

    public LoopFlatParallelizationRefactoring getLoopFlatParallelizationRefactoring() {
	return (LoopFlatParallelizationRefactoring) getRefactoring();
    }
}
