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

package org.eclipse.imp.x10dt.refactoring.transforms;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.visit.NodeVisitor;

public class Simplifier extends NodeVisitor {
    private Map<Expr,Object> fEvalResults= new HashMap<Expr, Object>();

    @Override
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof Binary) {
            Binary b= (Binary) n;
            Binary.Operator op= b.operator();
            Expr left= b.left();
            Expr right= b.right();
            if (op == Binary.COND_AND || op == Binary.COND_OR) {
                if (fEvalResults.containsKey(left) && fEvalResults.containsKey(right)) {
                    Boolean leftVal= (Boolean) fEvalResults.get(left);
                    Boolean rightVal= (Boolean) fEvalResults.get(right);
                    Boolean result= (op == Binary.COND_AND) ? leftVal && rightVal : leftVal || rightVal;

                    fEvalResults.put(b, result);
                }
            } else if (op == Binary.EQ || op == Binary.GE || op == Binary.GT || op == Binary.LE || op == Binary.LT || op == Binary.NE) {
                if (left.type().isInt() && right.type().isInt() && fEvalResults.containsKey(left) && fEvalResults.containsKey(right)) {
                    Integer leftVal= (Integer) fEvalResults.get(left);
                    Integer rightVal= (Integer) fEvalResults.get(right);
                    boolean result= false;

                    if (op == Binary.EQ) {
                        result= leftVal == rightVal;
                    } else if (op == Binary.GE) {
                        result= leftVal >= rightVal;
                    } else if (op == Binary.GT) {
                        result= leftVal > rightVal;
                    } else if (op == Binary.LE) {
                        result= leftVal <= rightVal;
                    } else if (op == Binary.LT) {
                        result= leftVal < rightVal;
                    } else if (op == Binary.NE) {
                        result= leftVal != rightVal;
                    }
                    fEvalResults.put(b, result);
                }
            }
        } else if (n instanceof If) {
            If ifStmt= (If) n;
            Stmt thenStmt= ifStmt.consequent();
            Stmt elseStmt= ifStmt.alternative();
            Expr cond= ifStmt.cond();
            if (fEvalResults.containsKey(cond)) {
                return (Boolean) fEvalResults.get(cond) ? thenStmt : elseStmt;
            }
        } else if (n instanceof Expr) {
            Expr e= (Expr) n;
            if (e.isConstant()) {
                fEvalResults.put(e, e.constantValue());
            }
        }
        return super.leave(parent, old, n, v);
    }
}