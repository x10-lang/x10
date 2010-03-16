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

package org.eclipse.imp.x10dt.refactoring.actions;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.imp.x10dt.refactoring.RenameRefactoring;
import org.eclipse.imp.x10dt.refactoring.wizards.RenameWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class RenameRefactoringAction extends TextEditorAction {
    public RenameRefactoringAction(ITextEditor editor) {
        super(X10RefactoringMessages.ResBundle, "rename.", editor);
    }

    public void run() {
        final Shell shell= getTextEditor().getSite().getShell();

        RenameRefactoring refactoring= new RenameRefactoring(getTextEditor());

        if (refactoring != null) {
            new RefactoringStarter().activate(refactoring, new RenameWizard(refactoring), shell, refactoring.getName(), false);
        }
    }
}
