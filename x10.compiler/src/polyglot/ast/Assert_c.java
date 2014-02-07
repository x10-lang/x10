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

package polyglot.ast;

import java.util.List;

import polyglot.frontend.Globals;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * An <code>Assert</code> is an assert statement.
 */
public class Assert_c extends Stmt_c implements Assert
{
    protected Expr cond;
    protected Expr errorMessage;

    public Assert_c(Position pos, Expr cond, Expr errorMessage) {
	super(pos);
	assert(cond != null); // errorMessage may be null
	this.cond = cond;
	this.errorMessage = errorMessage;
    }

    /** Get the condition to check. */
    public Expr cond() {
	return this.cond;
    }

    /** Set the condition to check. */
    public Assert cond(Expr cond) {
	Assert_c n = (Assert_c) copy();
	n.cond = cond;
	return n;
    }

    /** Get the error message to report. */
    public Expr errorMessage() {
	return this.errorMessage;
    }

    /** Set the error message to report. */
    public Assert errorMessage(Expr errorMessage) {
	Assert_c n = (Assert_c) copy();
	n.errorMessage = errorMessage;
	return n;
    }

    /** Reconstruct the statement. */
    protected Assert_c reconstruct(Expr cond, Expr errorMessage) {
	if (cond != this.cond || errorMessage != this.errorMessage) {
	    Assert_c n = (Assert_c) copy();
	    n.cond = cond;
	    n.errorMessage = errorMessage;
	    return n;
	}

	return this;
    }

    public Node typeCheck(ContextVisitor tc) {
        if (! cond.type().isBoolean()) {
            Errors.issue(tc.job(),
                    new SemanticException("Condition of assert statement must have boolean type.", cond.position()),
                    this);
        }

        if (errorMessage != null && errorMessage.type().isVoid()) {
            Errors.issue(tc.job(),
                    new SemanticException("Error message in assert statement cannot be void.", errorMessage.position()),
                    this);
        }

        return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr cond = (Expr) visitChild(this.cond, v);
	Expr errorMessage = (Expr) visitChild(this.errorMessage, v);
	return reconstruct(cond, errorMessage);
    }

    public String toString() {
	return "assert " + cond.toString() +
                (errorMessage != null
                    ? ": " + errorMessage.toString() : "") + ";";
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("assert ");
	print(cond, w, tr);

        if (errorMessage != null) {
            w.write(": ");
            print(errorMessage, w, tr);
        }

        w.write(";");
    }

    public void translate(CodeWriter w, Translator tr) {
        if (! tr.job().extensionInfo().getOptions().assertions) {
            w.write(";");
        }
        else {
            prettyPrint(w, tr);
        }
    }

    public Term firstChild() {
        return cond;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (errorMessage != null) {
            v.visitCFG(cond, errorMessage, ENTRY);
            v.visitCFG(errorMessage, this, EXIT);
        }
        else {
            v.visitCFG(cond, this, EXIT);
        }

        return succs;
    }

}
