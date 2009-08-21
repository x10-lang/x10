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

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class RenameWizard extends RefactoringWizard {
    public RenameWizard(RenameRefactoring refactoring, String pageTitle) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(pageTitle);
    }

    protected void addUserInputPages() {
        RenameInputPage page= new RenameInputPage("Rename", (RenameRefactoring) this.getRefactoring());

        addPage(page);
    }

    public RenameRefactoring getRenameRefactoring() {
        return (RenameRefactoring) getRefactoring();
    }
}
