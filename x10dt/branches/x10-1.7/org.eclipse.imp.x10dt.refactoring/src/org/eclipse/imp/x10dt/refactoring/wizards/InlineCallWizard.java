/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.refactoring.wizards;

import org.eclipse.imp.x10dt.refactoring.X10RefactoringBase;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class InlineCallWizard extends RefactoringWizard {
    public InlineCallWizard(X10RefactoringBase refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        InlineCallInputPage page= new InlineCallInputPage(getRefactoring().getName());

        addPage(page);
    }

    public X10RefactoringBase getInlineCallRefactoring() {
        return (X10RefactoringBase) getRefactoring();
    }
}
