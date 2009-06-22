/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.refactoring.actions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;


public class RefactoringContributor implements IRefactoringContributor {
    public RefactoringContributor() { }

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
	return new IAction[] {new RenameRefactoringAction(editor),
	};
    }
}
