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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An immutable representation of a Java language <code>if</code> statement.
 * Contains an expression whose value is tested, a ``then'' statement 
 * (consequent), and optionally an ``else'' statement (alternate).
 */
public abstract class If_c extends Stmt_c implements If
{
    protected Expr cond;
    protected Stmt consequent;
    protected Stmt alternative;

    public If_c(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
	super(pos);
	assert(cond != null && consequent != null); // alternative may be null;
	this.cond = cond;
	this.consequent = consequent;
	this.alternative = alternative;
    }

    /** Get the conditional of the statement. */
    public Expr cond() {
	return this.cond;
    }

    /** Set the conditional of the statement. */
    public If cond(Expr cond) {
	If_c n = (If_c) copy();
	n.cond = cond;
	return n;
    }

    /** Get the consequent of the statement. */
    public Stmt consequent() {
	return this.consequent;
    }

    /** Set the consequent of the statement. */
    public If consequent(Stmt consequent) {
	If_c n = (If_c) copy();
	n.consequent = consequent;
	return n;
    }

    /** Get the alternative of the statement. */
    public Stmt alternative() {
	return this.alternative;
    }

    /** Set the alternative of the statement. */
    public If alternative(Stmt alternative) {
	If_c n = (If_c) copy();
	n.alternative = alternative;
	return n;
    }

    /** Reconstruct the statement. */
    protected If_c reconstruct(Expr cond, Stmt consequent, Stmt alternative) {
	if (cond != this.cond || consequent != this.consequent || alternative != this.alternative) {
	    If_c n = (If_c) copy();
	    n.cond = cond;
	    n.consequent = consequent;
	    n.alternative = alternative;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr cond = (Expr) visitChild(this.cond, v);
	Node consequent = visitChild(this.consequent, v);
        if (consequent instanceof NodeList)
          consequent = ((NodeList) consequent).toBlock();
	Node alternative = visitChild(this.alternative, v);
        if (alternative instanceof NodeList)
          alternative = ((NodeList) alternative).toBlock();
	return reconstruct(cond, (Stmt) consequent, (Stmt) alternative);
    }

    /** Type check the statement. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
	return "if (" + cond + ") " + consequent +
	    (alternative != null ? " else " + alternative : "");
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {    
	w.write("if (");
	printBlock(cond, w, tr);
	w.write(")");
       
	printSubStmt(consequent, w, tr);

	if (alternative != null) {
	    if (consequent instanceof Block) {
		// allow the "} else {" formatting
		w.write(" ");
	    } else {
		w.allowBreak(0, " ");
	    }

            if (alternative instanceof Block) {
		w.write ("else ");
		print(alternative, w, tr);
	    } else {
		w.begin(4);
		w.write("else");
		printSubStmt(alternative, w, tr);
		w.end();
	    }
	}
    }

    public Term firstChild() {
        return cond;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (alternative == null) {
            v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent, 
                             ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
            v.visitCFG(consequent, this, EXIT);
        }
        else {
            v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent,
                             ENTRY, FlowGraph.EDGE_KEY_FALSE, alternative, ENTRY);
            v.visitCFG(consequent, this, EXIT);
            v.visitCFG(alternative, this, EXIT);
        }

        return succs;
    }
    

}
