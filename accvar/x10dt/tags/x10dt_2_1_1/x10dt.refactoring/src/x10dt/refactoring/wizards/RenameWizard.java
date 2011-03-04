/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.refactoring.wizards;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import x10dt.refactoring.RenameRefactoring;

public class RenameWizard extends RefactoringWizard {
    public RenameWizard(RenameRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        RenameInputPage page= new RenameInputPage(getRefactoring().getName(), (RenameRefactoring) getRefactoring());

        addPage(page);
    }

    public RenameRefactoring getRenameRefactoring() {
        return (RenameRefactoring) getRefactoring();
    }
}
