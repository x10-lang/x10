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

package org.eclipse.imp.x10dt.refactoring.changes;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.LocalDecl;
import polyglot.ast.Stmt;

/**
 * Inserts a new local var decl
 */
public class InsertLocalDeclChange extends Change {
    private final LocalDecl fLocalDecl;
    private final Block fDeclParent;
    private final Stmt fBefore;

    /**
     * @param name
     */
    public InsertLocalDeclChange(String chgName, LocalDecl localDecl, Block parent, Stmt before /* null => last */) {
        super(chgName);
        fLocalDecl= localDecl;
        fDeclParent= parent;
        fBefore= before /* null => last */;
    }

    protected LocalDecl getLocalDecl() {
        return fLocalDecl;
    }

    protected Block getDeclParent() {
        return fDeclParent;
    }

    protected Stmt getBefore() {
        return fBefore;
    }

    /* (non-Javadoc)
     * @see org.eclipse.imp.x10dt.refactoring.changes.Change#getDescriptor()
     */
    @Override
    public ChangeDescriptor getDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.imp.x10dt.refactoring.changes.Change#getModifiedElements()
     */
    @Override
    public List getModifiedElements() {
        // TODO Auto-generated method stub
        return null;
    }
}
