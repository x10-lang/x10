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
import polyglot.ast.Empty_c;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.While;
import polyglot.util.Position;
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
            } else if (op == Binary.ADD || op == Binary.BIT_AND || op == Binary.BIT_OR || op == Binary.BIT_XOR || op == Binary.DIV || op == Binary.MOD || op == Binary.MUL || op == Binary.SHL || op == Binary.SHR || op == Binary.SUB) {
                if (left.type().isInt() && right.type().isInt() && fEvalResults.containsKey(left) && fEvalResults.containsKey(right)) {
                    Integer leftVal= (Integer) fEvalResults.get(left);
                    Integer rightVal= (Integer) fEvalResults.get(right);
                    int result= 0;

                    if (op == Binary.ADD) {
                        result= leftVal + rightVal;
                    } else if (op == Binary.BIT_AND) {
                        result= leftVal & rightVal;
                    } else if (op == Binary.BIT_OR) {
                        result= leftVal | rightVal;
                    } else if (op == Binary.BIT_XOR) {
                        result= leftVal ^ rightVal;
                    } else if (op == Binary.DIV) {
                        result= leftVal / rightVal;
                    } else if (op == Binary.MOD) {
                        result= leftVal % rightVal;
                    } else if (op == Binary.MUL) {
                        result= leftVal * rightVal;
                    } else if (op == Binary.SHL) {
                        result= leftVal << rightVal;
                    } else if (op == Binary.SHR) {
                        result= leftVal >> rightVal;
                    } else if (op == Binary.SUB) {
                        result= leftVal - rightVal;
                    } else if (op == Binary.USHR) {
                        result= leftVal >>> rightVal;
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
        } else if (n instanceof While) {
            While w= (While) n;
            Expr cond= w.cond();
            // while (false) S => <>
            if (fEvalResults.containsKey(cond) && !((Boolean) fEvalResults.get(cond)).booleanValue()) {
                return new Empty_c(Position.COMPILER_GENERATED);
            }
        }
        return super.leave(parent, old, n, v);
    }
}