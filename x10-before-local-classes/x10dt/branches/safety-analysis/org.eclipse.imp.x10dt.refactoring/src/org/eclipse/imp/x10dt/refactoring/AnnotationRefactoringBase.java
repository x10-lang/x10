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

package org.eclipse.imp.x10dt.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;

/**
 *
 */
public abstract class AnnotationRefactoringBase extends X10RefactoringBase {
    /**
     * If true, the refactoring simply adds a transformation annotation to the source code
     * indicating the transformation to be performed;
     * otherwise, the transformation is effected directly on the source.
     */
    protected boolean fByAnnotation;

    /**
     * @param editor
     */
    public AnnotationRefactoringBase(ITextEditor editor) {
        super(editor);
    }

    public boolean getByAnnotation() {
        return fByAnnotation;
    }

    public void setByAnnotation(boolean byAnnotation) {
        fByAnnotation= byAnnotation;
    }
}
