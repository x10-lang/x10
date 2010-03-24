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

package x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id_c;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Field_c;
import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import x10.ast.X10Loop.LoopKind;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.types.X10Context;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.XConstrainedTerm;

/**
 * An immutable representation of the X10 statement: ateach (i : D) S
 * @author vj Dec 9, 2004
 * @author Christian Grothoff
 */
public class AtEach_c extends X10ClockedLoop_c implements AtEach, Clocked {

	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
		loopKind=LoopKind.ATEACH;
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param clocks
	 * @param body
	 */
	public AtEach_c(Position pos, Formal formal, Expr domain, List clocks, Stmt body) {
		super(pos, formal, domain, clocks, body);
		loopKind=LoopKind.ATEACH;
	}

	public Expr getDomain(Expr d) {
		return new Field_c(position(), d, new Id_c(position(), Name.make("dist")));
	}

	@Override
	public Context enterChildScope(Node child, Context c) {
		X10Context xc = (X10Context) super.enterChildScope(child, c);
		CConstraint d = new CConstraint_c();
		XTerm term = XTerms.makeUQV();
		try {
			// FIXME: this creates a new place term; ideally, it should be the place associated with each
			// point in the ateach distribution 
			xc = (X10Context) xc.pushPlace(XConstrainedTerm.instantiate(d, term));
		} 
		catch (XFailure z) {
			throw new InternalCompilerError("Cannot construct placeTerm from " + term + " and constraint " + d + ".");
		}
        return xc;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public String toString() {
		return "ateach (" + formal + ":" + domain + ")" + (clocks != null ? " clocked(" + clocks + ")" : "") + body;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("ateach(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		if (clocks != null) {
			w.write("clocked(");
			for (Iterator c = clocks.iterator(); c.hasNext(); ) {
				print((Expr)c.next(), w, tr);
				if (c.hasNext()) w.write(", ");
			}
			w.write(")");
		}
		printSubStmt(body, w, tr);
	}
}
