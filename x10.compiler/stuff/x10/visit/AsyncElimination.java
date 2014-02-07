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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Assign;
import polyglot.ast.Receiver;
import polyglot.ast.Call;
import polyglot.ast.ProcedureCall;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.Future;

/**
 * The <code>AsyncElimination</code> runs over the AST and 
 * 'inlines' the Async blocks in the current activity in case 
 * the body of the async performs only a simple remote read / or write.
 * 
 * This optimization can render the results of the sampling incorrect
 * because remote accesses may disappear in the code and look like 
 * local accesses! 
 *
 * Note that this optimization is not legal in real X10 programs!
 * We use it just for the simple shared memory variant of the X10
 * prototype system to reduce the overheads of the very general 
 * implementation of async and future. 
 */
public class AsyncElimination extends NodeVisitor {
    private final boolean DEBUG_ = false;
    
    public Node leave(Node old, Node n, NodeVisitor v) {
        Node ret = n;

        // If we have a labeled block consisting of just one statement, then
        // flatten the block and label the statement instead. We also flatten
        // labeled blocks when there is no reference to the label within the
        // block.
        if (n instanceof Async) {
            Async as = (Async) n;
            Stmt as_body = as.body(); 
            Stmt simple_stmt = checkIfSimpleBlock_(as_body);
            if (simple_stmt != null && isOptimizableStmt_(simple_stmt)) {
                // replace the whole sync stmt with the simple smt;
                if (DEBUG_)
                    System.out.println("AsyncElimination - for async=" + n);
                ret = simple_stmt;
            }
        } else if (n instanceof Call) { // Futures that are forced
            Call c = (Call) n;
            List args = c.arguments();
            Receiver r = c.target();
            if (("force".equals(c.name().id()) || "apply".equals(c.name().id())) && args.size() == 0 && r instanceof Future) {
                Future f = (Future) r;
                Block f_block = f.body();
                if (isOptimizableClosureBlock_(f_block)) {
                    if (DEBUG_)
                        System.out.println("AsyncElimination - for future=" + n);
                    ret = getReturnExpr_(f_block);
                }
            }
        }
        return ret;
    }

    /**
     * returns the single statement that may be replaced for the async 
     * */
    private Stmt checkIfSimpleBlock_(Stmt s) {
        Stmt the_one_stmt = null;
        if (s instanceof Block) {
            Block b = (Block) s;
            List l = b.statements();
            if (l.size() == 1) 
                the_one_stmt = (Stmt) l.get(0);
        } else {
            the_one_stmt = s;
        }
        return the_one_stmt;
    }
    
    /**
     * The stmt in the body of an async is optimizable if it is 
     * a simple assignment and the rhs meets the criteraia of
     * method isOptimizableExpr_.
     */
    private boolean isOptimizableStmt_(Stmt s) {
        boolean ret = false;
        if (s instanceof Eval) {
            Eval e = (Eval) s;
            Expr e_expr = e.expr();
            if (e_expr instanceof Assign) {
                Assign a_expr = (Assign) e_expr;
                ret = isOptimizableExpr_(a_expr.right());
            }
        } 
        return ret;
    }
    
    /**
     * The closure block is optimizable if it is empty or just returns an optimizable expr.
     */
    private boolean isOptimizableClosureBlock_(Block b) {
	    return getReturnExpr_(b) != null;
    }
    private Expr getReturnExpr_(Block b) {
	    if (b.statements().size() == 1) {
		    Stmt s = b.statements().get(0);
		    if (s instanceof Return) {
			    Return r = (Return) s;
			    if (r.expr() != null)
				    return r.expr();
		    }
	    }
	    return null;
    }
    /**
     * Traverses an expression and determines if it does not
     *  - invoke another method
     *  - is future
     *  - does not allocate other objects / arrays
     **/
    private boolean isOptimizableExpr_(Expr e) {
        final Set<Node> critical = CollectionFactory.newHashSet();
        e.visit( new NodeVisitor() {
            public Node leave( Node old, Node n, NodeVisitor v) {
                if ( n instanceof ProcedureCall ||
                     n instanceof Future ||
                     n instanceof New ||    // implies call to constructor
                     n instanceof NewArray) // implies execution of initializer
                {
                    critical.add(n);
                }
                return n;
            }
        });        
        return critical.isEmpty();
    }

}
