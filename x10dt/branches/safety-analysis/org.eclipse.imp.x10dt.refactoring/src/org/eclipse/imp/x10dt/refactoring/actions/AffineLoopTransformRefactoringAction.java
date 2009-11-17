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

package org.eclipse.imp.x10dt.refactoring.actions;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.imp.x10dt.refactoring.AffineLoopTransformRefactoring;
import org.eclipse.imp.x10dt.refactoring.wizards.AffineLoopTransformWizard;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class AffineLoopTransformRefactoringAction extends TextEditorAction {
    public AffineLoopTransformRefactoringAction(ITextEditor editor) {
        super(X10RefactoringMessages.ResBundle, "affineLoopTransform.", editor);
    }

    public void run() {
        final AffineLoopTransformRefactoring refactoring = new AffineLoopTransformRefactoring(getTextEditor());

        if (refactoring != null)
            new RefactoringStarter().activate(refactoring, new AffineLoopTransformWizard(refactoring),
                    getTextEditor().getSite().getShell(), refactoring.getName(), false);
    }
}
