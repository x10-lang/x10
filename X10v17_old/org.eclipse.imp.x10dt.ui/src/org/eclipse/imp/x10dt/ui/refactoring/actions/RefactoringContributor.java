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
