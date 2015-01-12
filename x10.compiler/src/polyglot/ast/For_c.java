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

import java.util.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * An immutable representation of a Java language <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public class For_c extends Loop_c implements For
{
    protected List<ForInit> inits;
    protected Expr cond;
    protected List<ForUpdate> iters;
    protected Stmt body;

    public For_c(Position pos, List<ForInit> inits, Expr cond, List<ForUpdate> iters, Stmt body) {
	super(pos);
	assert(inits != null && iters != null && body != null); // cond may be null, inits and iters may be empty
	this.inits = TypedList.copyAndCheck(inits, ForInit.class, true);
	this.cond = cond;
	this.iters = TypedList.copyAndCheck(iters, ForUpdate.class, true);
	this.body = body;
    }

    /** List of initialization statements */
    public List<ForInit> inits() {
	return Collections.unmodifiableList(this.inits);
    }

    /** Set the inits of the statement. */
    public For inits(List<ForInit> inits) {
	For_c n = (For_c) copy();
	n.inits = TypedList.copyAndCheck(inits, ForInit.class, true);
	return n;
    }

    /** Loop condition */
    public Expr cond() {
	return this.cond;
    }

    /** Set the conditional of the statement. */
    public For cond(Expr cond) {
	For_c n = (For_c) copy();
	n.cond = cond;
	return n;
    }

    /** List of iterator expressions. */
    public List<ForUpdate> iters() {
	return Collections.unmodifiableList(this.iters);
    }

    /** Set the iterator expressions of the statement. */
    public For iters(List<ForUpdate> iters) {
	For_c n = (For_c) copy();
	n.iters = TypedList.copyAndCheck(iters, ForUpdate.class, true);
	return n;
    }

    /** Loop body */
    public Stmt body() {
	return this.body;
    }

    /** Set the body of the statement. */
    public For body(Stmt body) {
	For_c n = (For_c) copy();
	n.body = body;
	return n;
    }

    /** Reconstruct the statement. */
    protected For_c reconstruct(List<ForInit> inits, Expr cond, List<ForUpdate> iters, Stmt body) {
	if (! CollectionUtil.allEqual(inits, this.inits) || cond != this.cond || ! CollectionUtil.allEqual(iters, this.iters) || body != this.body) {
	    For_c n = (For_c) copy();
	    n.inits = TypedList.copyAndCheck(inits, ForInit.class, true);
	    n.cond = cond;
	    n.iters = TypedList.copyAndCheck(iters, ForUpdate.class, true);
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	List<ForInit> inits = visitList(this.inits, v);
	Expr cond = (Expr) visitChild(this.cond, v);
	List<ForUpdate> iters = visitList(this.iters, v);
        Node body = visitChild(this.body, v);
	if (body instanceof NodeList) body = ((NodeList) body).toBlock();
	return reconstruct(inits, cond, iters, (Stmt) body);
    }

    public Context enterScope(Context c) {
	return c.pushBlock();
    }

    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) {
	TypeSystem ts = tc.typeSystem();

        // Check that all initializers have the same type.
        // This should be enforced by the parser, but check again here,
        // just to be sure.
        Type t = null;

        for (ForInit s : inits) {
            if (s instanceof LocalDecl) {
                LocalDecl d = (LocalDecl) s;
                Type dt = d.type().type();
                if (t == null) {
                    t = dt;
                }
                else if (! t.typeEquals(dt, tc.context())) {
                    throw new InternalCompilerError("Local variable " +
                        "declarations in a for loop initializer must all " +
                        "be the same type, in this case " + t + ", not " +
                        dt + ".", d.position());
                }
            }
        }

	if (cond != null &&
	    ! ts.isImplicitCastValid(cond.type(), ts.Boolean(), tc.context())) {
	    Errors.issue(tc.job(),
	            new SemanticException("The condition of a for statement must have boolean type.",cond.position()),
	            this);
	}

	return this;
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("for (");
	w.begin(0);

	if (inits != null) {
	    boolean first = true;
	    for (ForInit s : inits) {
	        if (!first) {
	            w.write(",");
	            w.allowBreak(2, " ");
	        }
	        printForInit(s, w, tr, first);
	        first = false;
	    }
	}

	w.write(";"); 
	w.allowBreak(0);

	if (cond != null) {
	    printBlock(cond, w, tr);
	}

	w.write (";");
	w.allowBreak(0);
	
	if (iters != null) {
	    for (Iterator<ForUpdate> i = iters.iterator(); i.hasNext();) {
		ForUpdate s = i.next();
		printForUpdate(s, w, tr);
		
		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(2, " ");
		}
	    }
	}

	w.end();
	w.write(")");

	printSubStmt(body, w, tr);
    }

    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("for (");
	String sep = "";
	for (ForInit s : inits()) {
	    sb.append(sep);
	    sep = ", ";
	    sb.append(s);
	}
	sb.append("; ");
	if (cond() != null)
	    sb.append(cond());
	sb.append("; ");
	sep = "";
	for (ForUpdate s : iters()) {
	    sb.append(sep);
	    sep = ", ";
	    sb.append(s);
	}
	sb.append(") ");
	sb.append(body());
	return sb.toString();
    }

    private void printForInit(ForInit s, CodeWriter w, PrettyPrinter tr, boolean printType) {
        boolean oldSemiColon = tr.appendSemicolon(false);
        boolean oldPrintType = tr.printType(printType);
        printBlock(s, w, tr);
        tr.printType(oldPrintType);
        tr.appendSemicolon(oldSemiColon);
    }

    private void printForUpdate(ForUpdate s, CodeWriter w, PrettyPrinter tr) {
        boolean oldSemiColon = tr.appendSemicolon(false);
        printBlock(s, w, tr);
        tr.appendSemicolon(oldSemiColon);
    }

    public Term firstChild() {
        return listChild(inits, cond != null ? (Term) cond : body);
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFGList(inits, cond != null ? (Term) cond : body, ENTRY);

        if (cond != null) {
            if (condIsConstantTrue()) {
                v.visitCFG(cond, body, ENTRY);
            }
            else {
                v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, body, 
                                 ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
            }
        }

        v.push(this).visitCFG(body, continueTarget(), ENTRY);
        v.visitCFGList(iters, cond != null ? (Term) cond : body, ENTRY);

        return succs;
    }

    public Term continueTarget() {
        return listChild(iters, cond != null ? (Term) cond : body);
    }

}
