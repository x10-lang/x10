/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10c.visit;

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
import x10.ast.X10NodeFactory;
import x10.types.X10TypeSystem;
import x10.visit.ExpressionFlattener;

public class ExpressionFlattenerForAtExpr extends ContextVisitor {
    
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    private Type imc;

    public ExpressionFlattenerForAtExpr(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Stmt) {
            
            class AtExprChecker extends NodeVisitor {
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
            
            AtExprChecker checker = new AtExprChecker();
            
            n.visitChildren(checker);
            
            if (checker.isContainAtExpr()) {
                return n.visit(new ExpressionFlattener(job, xts, xnf).context(context));
            }
        }
        return n;
    }
}