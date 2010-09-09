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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CompositeChange extends Change {
    private final List<Change> fChanges= new LinkedList<Change>();

    public CompositeChange(String name) {
        super(name);
    }

    public void add(Change change) {
        fChanges.add(change);
    }

    public void addAll(Change[] changes) {
        for(Change change: changes) {
            fChanges.add(change);
        }
    }

    public void addAll(List<Change> changes) {
        fChanges.addAll(changes);
    }

    public List<Change> getChildren() {
        return Collections.unmodifiableList(fChanges);
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        List<ChangeDescriptor> cds= new ArrayList<ChangeDescriptor>(fChanges.size());

        for(Change change: fChanges) {
            cds.add(change.getDescriptor());
        }
        CompositeChangeDescriptor ccd= new CompositeChangeDescriptor(cds);
        return ccd;
    }

    @Override
    public List getModifiedElements() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append("<composite: ");
        for(Change chg: fChanges) {
            if (chg == fChanges.get(0))
                sb.append(",");
            sb.append(chg);
        }
        sb.append(">");
        return sb.toString();
    }
}
