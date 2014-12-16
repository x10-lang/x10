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

package x10.ast;

import java.util.List;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.types.Context;
import polyglot.types.VarDef;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.types.Context;

/** An immutable class representing the X10 finish S construct.
 *  No special type-checking rules. 
 * @author vj Dec 20, 2004
 * 
 */
public class Finish_c extends Stmt_c implements Finish {
    protected Stmt body;
    protected boolean clocked;
    /**
     * @param pos
     */
    public Finish_c(Position pos) {
	super(pos);
    }

    public Finish_c(Position pos, Stmt body, boolean clocked) {
	super(pos);
	this.body = body;
	this.clocked = clocked; 
    }

    public boolean clocked() {
    	return clocked;
    }
    /* (non-Javadoc)
     * @see x10.ast.Finish#body()
     */
    public Stmt body() {
	return this.body;
    }
	
    /** Set the body of the statement. */
    public Finish body(Stmt body) {
	if (body == this.body) return this;
	Finish_c n = (Finish_c) copy();
	n.body = body;
	return n;
    }

    @Override
	public Context enterChildScope(Node child, Context c) {
    	c = super.enterChildScope(child,c);
        c=((Context) c).pushFinishScope(clocked);
    	addDecls(c);
    	return c;
	}
    public String toString() {
	return (clocked ? "clocked " : "") + "finish  { ... }";
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write((clocked ? "clocked " : "") + "finish ");
	printSubStmt(body, w, tr);
    }

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
	return (body != null ? body : null);
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
	v.visitCFG(body, this, EXIT);
	return succs;
    }
    /** Visit the children of the statement. */
    public Node visitChildren( NodeVisitor v ) {
	Stmt body = (Stmt) visitChild(this.body, v);
	// TODO: reconstruct
	return body(body);
    }

}
