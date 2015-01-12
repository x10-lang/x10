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

import polyglot.types.Context;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>Block</code> represents a Java block statement -- an immutable
 * sequence of statements.
 */
public abstract class AbstractBlock_c extends Stmt_c implements Block
{
    protected List<Stmt> statements;

    public AbstractBlock_c(Position pos, List<Stmt> statements) {
	super(pos);
	assert(statements != null);
	this.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
    }

    /** Get the statements of the block. */
    public List<Stmt> statements() {
	return this.statements;
    }

    /** Set the statements of the block. */
    public Block statements(List<Stmt> statements) {
	AbstractBlock_c n = (AbstractBlock_c) copy();
	n.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
	return n;
    }

    /** Append a statement to the block. */
    public Block append(Stmt stmt) {
	List<Stmt> l = new ArrayList<Stmt>(statements.size()+1);
	l.addAll(statements);
	l.add(stmt);
	return statements(l);
    }

    /** Prepend a statement to the block. */
    public Block prepend(Stmt stmt) {
        List<Stmt> l = new ArrayList<Stmt>(statements.size()+1);
        l.add(stmt);
        l.addAll(statements);
        return statements(l);
    }

    /** Reconstruct the block. */
    protected AbstractBlock_c reconstruct(List<Stmt> statements) {
	if (! CollectionUtil.allEqual(statements, this.statements)) {
	    AbstractBlock_c n = (AbstractBlock_c) copy();
	    n.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the block. */
    public Node visitChildren(NodeVisitor v) {
        List<Stmt> statements = visitList(this.statements, v);
        return reconstruct(statements);
    }

    public Context enterScope(Context c) {
	return c.pushBlock();
    }

    /** Write the block to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.begin(0);

	for (Iterator<Stmt> i = statements.iterator(); i.hasNext();) {
	    Stmt n = i.next();

	    printBlock(n, w, tr);

	    if (i.hasNext()) {
		w.newline();
	    }
	}

	w.end();
    }

    public Term firstChild() {
        return listChild(statements, null);
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFGList(statements, this, EXIT);
        return succs;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");

        int count = 0;

        for (Stmt n : statements) {
            if (count++ > 2) {
                sb.append(" ...");
                break;
            }
            sb.append(" ");
            sb.append(n.toString());
        }

        sb.append(" }");
        return sb.toString();
    }
}
