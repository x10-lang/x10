/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/**
 * A visitor which maintains a context throughout the visitor's pass.  This is 
 * the base class of the disambiguation and type checking visitors.
 *
 * TODO: update this documentation.
 * For a node <code>n</code> methods are called in this order:
 * <pre>
 * v.enter(n)
 *   v.enterScope(n);
 *     c' = n.enterScope(c)
 *   v' = copy(v) with c' for c
 * n' = n.visitChildren(v')
 * v.leave(n, n', v')
 *   v.addDecls(n')
 *     n.addDecls(c)
 * </pre>
 */
public class ContextVisitor extends ErrorHandlingVisitor
{
    protected ContextVisitor outer;
    
    /** The current context of this visitor. */
    protected Context context;

    public ContextVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        this.outer = null;
        this.context = null;
    }

    @Override
    public NodeVisitor begin() {
        context = ts.emptyContext();
        outer = null;
        return super.begin();
    }

    /** Returns the context for this visitor.
     *
     *  @return Returns the context that is currently in use by this visitor.
     *  @see polyglot.types.Context
     */
    public Context context() {
        return context;
    }

    /** Returns a new ContextVisitor that is a copy of the current visitor,
     *  except with an updated context.
     *
     *  @param c The new context that is to be used.
     *  @return Returns a copy of this visitor with the new context 
     *  <code>c</code>.
     */
    public ContextVisitor context(Context c) {
        ContextVisitor v = (ContextVisitor) this.shallowCopy();
        v.context = c;
        return v;
    }

    /**
     * Returns a new context based on the current context, the Node current 
     * being visited (<code>parent</code>), and the Node that is being 
     * entered (<code>n</code>).  This new context is to be used
     * for visiting <code>n</code>. 
     *
     * @return The new context after entering Node <code>n</code>.
     */
    protected Context enterScope(Node parent, Node n) {
        if (parent != null) {
            return parent.del().enterChildScope(n, context);
        }
        // no parent node yet.
        return n.del().enterScope(context);
    }
   
    /**
     * Imperatively update the context with declarations to be added after
     * visiting the node.
     */
    protected void addDecls(Node n) {
	    if (n != null)
		    n.addDecls(context);
    }
    
    @Override
    public final NodeVisitor enter(Node n) {
    	throw new InternalCompilerError("Cannot call enter(Node n) on a ContextVisitor; use enter(Node parent, Node n) instead");
    }
    
    @Override
    public final NodeVisitor enter(Node parent, Node n) {
        if (reporter.should_report(Reporter.visit, 5))
            reporter.report(5, "enter(" + n + ")");

        if (prune) {
            return new PruningVisitor();
        }

        ContextVisitor v = this;

        Context c = this.enterScope(parent, n);

        if (c != this.context) {
            v = (ContextVisitor) this.shallowCopy();
            v.context = c;
            v.outer = this;
            v.error = false;
        }

        return v.superEnter(parent, n);
    }

    protected boolean prune;

    public final NodeVisitor superEnter(Node parent, Node n) {
        return super.enter(parent, n);
    }

    @Override
    public final Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        // If the traversal was pruned, just return n since leaveCall
        // might expect a ContextVisitor, not a PruningVisitor.
        if (v instanceof PruningVisitor || prune) {
            return n;
        }

        Node m = super.leave(parent, old, n, v);
        this.addDecls(m);
        return m;
    }

    @Override
    public final Node leave(Node old, Node n, NodeVisitor v) {
        throw new InternalCompilerError("ContextVisitor.leave(Node, Node, NodeVisitor) should not be called");
    }
}
