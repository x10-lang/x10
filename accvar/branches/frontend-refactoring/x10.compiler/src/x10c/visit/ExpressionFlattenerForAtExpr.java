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

import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtExpr;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.Stmt;
import x10.ast.NodeFactory;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.TypeSystem;
import x10.types.TypeSystem;
import x10.visit.ExpressionFlattener;

public class ExpressionFlattenerForAtExpr extends ContextVisitor {
    
    private final TypeSystem xts;
    private final NodeFactory xnf;

    private Type imc;

    public ExpressionFlattenerForAtExpr(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
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