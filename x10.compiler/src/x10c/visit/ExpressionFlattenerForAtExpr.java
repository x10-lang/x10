/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10c.visit;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtExpr;
import x10.ast.StmtSeq;
import x10.visit.ExpressionFlattener;

public class ExpressionFlattenerForAtExpr extends ContextVisitor {
    
    private final TypeSystem xts;
    private final NodeFactory xnf;

    public ExpressionFlattenerForAtExpr(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Loop) {
            AtExprChecker checker = new AtExprChecker();
            Expr cond = ((Loop) n).cond();
            if (cond != null) {
                if (cond instanceof StmtSeq) {
                    List<Stmt> stmts = ((StmtSeq) cond).statements();
                    for (Stmt stmt : stmts) {
                        stmt.visitChildren(checker);
                    }
                }
            }
            if (n instanceof For) {
                For for1 = (For) n;
                for (ForInit forInit : for1.inits()) {
                    if (forInit instanceof StmtSeq) {
                        List<Stmt> stmts = ((StmtSeq) forInit).statements();
                        for (Stmt stmt : stmts) {
                            stmt.visitChildren(checker);
                        }
                    }
                }
                for (ForUpdate forUpdate : for1.iters()) {
                    if (forUpdate instanceof StmtSeq) {
                        List<Stmt> stmts = ((StmtSeq) forUpdate).statements();
                        for (Stmt stmt : stmts) {
                            stmt.visitChildren(checker);
                        }
                    }
                }
            }
            if (checker.isContainAtExpr()) {
                return n.visit(new ExpressionFlattener(job, xts, xnf).context(context));
            }
        }
        else if (n instanceof Stmt) {
            AtExprChecker checker = new AtExprChecker();
            n.visitChildren(checker);
            if (checker.isContainAtExpr()) {
                return n.visit(new ExpressionFlattener(job, xts, xnf).context(context));
            }
        }
        return n;
    }

    private static class AtExprChecker extends NodeVisitor {
        boolean isContainAtExpr = false;
        
        @Override
        public Node override(Node parent, Node n) {
            if (n instanceof Stmt) {
                return n;
            }
            return null;
        }
        
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            if (n instanceof AtExpr) {
                isContainAtExpr = true;
            }
            return n;
        }
        
        private boolean isContainAtExpr() {
            return isContainAtExpr;
        }
    }
}