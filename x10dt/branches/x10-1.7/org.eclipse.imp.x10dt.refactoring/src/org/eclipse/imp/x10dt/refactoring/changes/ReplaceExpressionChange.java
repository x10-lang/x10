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

import polyglot.ast.Expr;
import polyglot.ast.Node;

/**
 *
 */
public class ReplaceExpressionChange extends Change {
    private final Expr fOldExpr;
    private final Expr fNewExpr;
    private final Node fExprParent;

    /**
     * @param name
     */
    public ReplaceExpressionChange(String name, Expr oldExpr, Node exprParent, Expr newExpr) {
        super(name);
        fOldExpr= oldExpr;
        fExprParent= exprParent;
        fNewExpr= newExpr;
    }

    protected Node getExprParent() {
        return fExprParent;
    }

    protected Expr getOldExpr() {
        return fOldExpr;
    }

    protected Expr getNewExpr() {
        return fNewExpr;
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

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append("<replace expr ");
        sb.append(fOldExpr);
        sb.append(" by expr ");
        sb.append(fNewExpr);
        sb.append(" in parent ");
        sb.append(fExprParent);
        sb.append(">");
        return sb.toString();
    }
}
