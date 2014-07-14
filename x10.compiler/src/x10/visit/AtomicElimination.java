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

package x10.visit;

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Assign;
import polyglot.ast.ArrayAccess;
import polyglot.ast.ProcedureCall;
import polyglot.ast.FieldAssign;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.LocalAssign;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.visit.NodeVisitor;
import x10.ast.Atomic;
import x10.ast.SettableAssign;

/**
 * The <code>AsyncElimination</code> runs over the AST and 
 * strips of 'atomic' form blocks that read or write only a single 
 * shared variable.
 * 
 * This transformation might cause programs with 
 * await statements to hang when atomic blocks that modify variables checked 
 * in await are removed (and hence also the notify associated with atomic).  
 * 
 * Note also that this optimization is not legal in real X10 programs!
 * We use it just for the simple shared memory variant of the X10
 * prototype system to reduce the overheads of the very general 
 * implementation of atomic. 
 **/
public class AtomicElimination extends NodeVisitor {
    private final boolean DEBUG_ = false;
    
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        Node ret = n;

        // If we have a labeled block consisting of just one statement, then
        // flatten the block and label the statement instead. We also flatten
        // labeled blocks when there is no reference to the label within the
        // block.
        if (n instanceof Atomic) {
            Atomic as = (Atomic) n;
            Stmt as_body = as.body(); 
            Stmt simple_stmt = checkIfSimpleBlock_(as_body);
            if (simple_stmt != null && isOptimizableStmt_(simple_stmt)) {
                // replace the whole atomic stmt with its body;
                if (DEBUG_)
                    System.out.println("AtomicElimination - for atomic=" + n);
                ret = simple_stmt;
            }
        } 
        return ret;
    }

    /**
     *  returns the single statement that may be replaced for the atomic 
     */
    private Stmt checkIfSimpleBlock_(Stmt s) {
        Stmt the_one_stmt = null;
        if (s instanceof Block) {
            Block b = (Block) s;
            List<Stmt> l = b.statements();
            if (l.size() == 1) 
                the_one_stmt = (Stmt) l.get(0);
        } else {
            the_one_stmt = s;
        }
        return the_one_stmt;
    }
    
    /**
     * The stmt in the body of atomic is optimizable if it is 
     * a simple assignment and the rhs meets the criteria of
     * method isOptimizableExpr_.
     * 
     * For writes to shared variables, the atomic block must be kept, because 
     * the write might lead to a notfiy that is necessary to wake-up an activity 
     * that is blocked on await.
     */
    private boolean isOptimizableStmt_(Stmt s) {
        boolean ret = false;
        if (s instanceof Eval) {
            Eval e = (Eval) s;
            Expr e_expr = e.expr();
            if (e_expr instanceof SettableAssign) {
        	SettableAssign a;
        	a = (SettableAssign) e_expr;
                ret = numFieldRefs_(a.array()) == 0 && // don't allow writes
		      numFieldRefs_(a.index()) == 0 && // don't allow writes
                      numFieldRefs_(a.right()) <= 1;
            }
            if (e_expr instanceof ArrayAccessAssign) {
        	ArrayAccessAssign a;
        	a = (ArrayAccessAssign) e_expr;
        	ret = numFieldRefs_(a.array()) == 0 && // don't allow writes 
        	      numFieldRefs_(a.index()) == 0 && // don't allow writes 
        	      numFieldRefs_(a.right()) <= 1;
            }
            if (e_expr instanceof FieldAssign) {
        	FieldAssign a;
        	a = (FieldAssign) e_expr;
        	ret = false;
            }
            if (e_expr instanceof LocalAssign) {
        	LocalAssign a;
        	a = (LocalAssign) e_expr;
        	ret = numFieldRefs_(a.right()) <= 1;
            }
        } 
        return ret;
    }
    
    /* cludge class */
    private class Ctr {
        int ctr;
    }
    private int numFieldRefs_(List<Expr> es) {
	int c = 0;
	for (Expr e : es) {
	    c += numFieldRefs_(e);
	}
	return c;
    }
    /**
     * Traverses an expression and determines if it does not
     *  - invoke another method
     *  - is future
     *  - does not allocate other objects / arrays
     **/
    private int numFieldRefs_(Expr e) {
        final Ctr c = new Ctr(); 
        e.visit( new NodeVisitor() {
            @Override
            public Node leave( Node old, Node n, NodeVisitor v) {
                if ( n instanceof Field ||
                     n instanceof ArrayAccess ||
                     n instanceof ArrayAccessAssign ||
                     n instanceof FieldAssign) 
                    c.ctr++;
                else if (n instanceof ProcedureCall ||
                         n instanceof New ||    // implies call to constructor
                         n instanceof NewArray) // implies execution of initializer) 
                    c.ctr += 2;
                return n;
            }
        });        
        return c.ctr;
    }
}
