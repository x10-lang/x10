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

import polyglot.ast.Stmt;

/**
 *
 */
public class ReplaceStatementChange extends Change {
    private final Stmt fOldStmt;
    private final Stmt fNewStmt;
    private final Stmt fParentStmt;

    /**
     * @param name
     */
    public ReplaceStatementChange(String name, Stmt old, Stmt newStmt, Stmt parent) {
        super(name);
        fOldStmt= old;
        fNewStmt= newStmt;
        fParentStmt= parent;
    }

    public Stmt getOldStmt() {
        return fOldStmt;
    }

    public Stmt getNewStmt() {
        return fNewStmt;
    }

    public Stmt getParentStmt() {
        return fParentStmt;
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
