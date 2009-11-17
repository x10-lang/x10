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

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

public class X10DTRefactoringContributor implements IRefactoringContributor {
    public X10DTRefactoringContributor() {}

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
        return new IAction[] {
                new MoveRefactoringAction(editor),
                new RenameRefactoringAction(editor),
                new MarkContextAction(editor),
                new ExtractAsyncRefactoringAction(editor),
                new ExtractAsyncInFinishRefactoringAction(editor),
                new InlineCallRefactoringAction(editor),
                new LoopFlatParallelizationRefactoringAction(editor),
                new LoopUnrollRefactoringAction(editor),
                new TileLoopRefactoringAction(editor),
                new StripMineLoopRefactoringAction(editor),
                new AffineLoopTransformRefactoringAction(editor)
            };
    }
}
