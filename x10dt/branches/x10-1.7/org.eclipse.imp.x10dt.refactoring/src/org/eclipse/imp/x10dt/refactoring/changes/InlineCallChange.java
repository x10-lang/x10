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

import polyglot.ast.Call;
import polyglot.ast.CodeBlock;

/**
 *
 */
public class InlineCallChange extends Change {
    private final Call fCall;
    private final CodeBlock fCallOwner;

    /**
     * @param name
     */
    public InlineCallChange(String name, Call call, CodeBlock owner) {
        super(name);
        fCall= call;
        fCallOwner= owner;
    }

    public Call getCall() {
        return fCall;
    }

    public CodeBlock getCallOwner() {
        return fCallOwner;
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
