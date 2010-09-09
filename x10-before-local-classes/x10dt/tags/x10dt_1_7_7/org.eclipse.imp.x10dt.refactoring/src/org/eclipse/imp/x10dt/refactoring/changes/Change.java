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

/**
 * Describes a (possibly composite) change to be applied to an AST.
 */
public abstract class Change {
    private final String fName;

    /**
     * true iff this particular change (which may be a sub-change in a composite change)
     * is enabled. Generally changes start out enabled, but a user may choose to disable
     * some portion of a composite change.
     */
    private boolean fEnabled;

    private Change fParent;

    protected boolean fIsValid;

    public Change(String name) {
        fName= name;
    }

    /**
     * @return the human-readable name of this change
     */
    public String getName() {
        return fName;
    }

    public abstract ChangeDescriptor getDescriptor();

    public abstract List getModifiedElements();

    /**
     * @return true if this change is enabled, i.e., should be performed.
     * Typically false only if the user explicitly specifies that this change not be made.
     */
    public boolean getEnabled() {
        return fEnabled;
    }

    public void setEnabled(boolean enabled) {
        fEnabled= enabled;
    }

    public Change getParent() {
        return fParent;
    }

    public void setParent(Change parent) {
        fParent= parent;
    }

    /**
     * @return true if this change is valid, i.e., can be performed in its present state.
     * Could be false, e.g., if this change requires write access to a read-only file.
     */
    public boolean getIsValid() {
        return fIsValid;
    }

    public void setValid(boolean isValid) {
        fIsValid= isValid;
    }
}
